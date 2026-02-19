package es.rafapuig.pmdm.compose.proyecto.presentation.reviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.rafapuig.pmdm.compose.proyecto.data.local.TokenManager
import es.rafapuig.pmdm.compose.proyecto.data.remote.ApiResponse
import es.rafapuig.pmdm.compose.proyecto.data.remote.api.BookApiService
import es.rafapuig.pmdm.compose.proyecto.data.remote.api.LibroListaApiService
import es.rafapuig.pmdm.compose.proyecto.data.remote.api.ListaApiService
import es.rafapuig.pmdm.compose.proyecto.data.remote.api.AuthApiService
import es.rafapuig.pmdm.compose.proyecto.data.remote.safeApiCall
import es.rafapuig.pmdm.compose.proyecto.data.remote.safeApiCallNoBody
import es.rafapuig.pmdm.compose.proyecto.domain.model.Review
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import es.rafapuig.pmdm.compose.proyecto.data.remote.dto.GoogleBookDto

class ReviewsViewModel(
    private val tokenManager: TokenManager,
    private val listaApiService: ListaApiService,
    private val libroListaApiService: LibroListaApiService,
    private val bookApiService: BookApiService,
    private val authApiService: AuthApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewsState())
    val uiState = _uiState.asStateFlow()

    // Caché de todas las listas con Deferred para evitar llamadas duplicadas
    private var allListasDeferred: Deferred<List<es.rafapuig.pmdm.compose.proyecto.data.remote.dto.ListaDto>>? = null
    private val allListasMutex = Mutex()

    init {
        // Lanzar ambas cargas en paralelo (cada una tiene su propio launch interno)
        // Ambas usan getAllListasCached() que comparte la misma petición
        loadReviews()
        loadPublicReviews()
    }

    private suspend fun getAllListasCached(): List<es.rafapuig.pmdm.compose.proyecto.data.remote.dto.ListaDto> {
        val deferred = allListasMutex.withLock {
            allListasDeferred ?: CompletableDeferred<List<es.rafapuig.pmdm.compose.proyecto.data.remote.dto.ListaDto>>().also { d ->
                allListasDeferred = d
                viewModelScope.launch {
                    val result = try {
                        val resp = safeApiCall { listaApiService.getAllListas() }
                        when (resp) {
                            is ApiResponse.Success -> resp.data
                            else -> emptyList()
                        }
                    } catch (_: Exception) { emptyList() }
                    d.complete(result)
                }
            }
        }
        return deferred.await()
    }

    fun onAction(action: ReviewsIntent) {
        when (action) {
            is ReviewsIntent.LoadReviews -> loadReviews()
            is ReviewsIntent.LoadPublicReviews -> {
                // Forzar recarga: resetear estado y caché
                _uiState.update { it.copy(isLoadingPublic = false, publicReviews = emptyList(), publicErrorMessage = null) }
                viewModelScope.launch {
                    allListasMutex.withLock { allListasDeferred = null }
                    loadPublicReviews()
                }
            }
            is ReviewsIntent.SelectTab -> {
                _uiState.update { it.copy(selectedTab = action.tab) }
                if (action.tab == 1 && _uiState.value.publicReviews.isEmpty()
                    && !_uiState.value.isLoadingPublic && _uiState.value.publicErrorMessage == null) {
                    loadPublicReviews()
                }
            }
            is ReviewsIntent.DeleteReview -> deleteReview(action.reviewId)
            is ReviewsIntent.SearchPublicReviews -> {
                _uiState.update { it.copy(publicSearchQuery = action.query) }
            }
        }
    }

    private fun loadReviews() {
        val userId = tokenManager.getUserId()
        if (userId == -1L) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "Inicia sesión para ver tus reseñas") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                // 1. Intentar obtener las listas del usuario desde la caché global
                var listas = getAllListasCached().filter { it.idUsuario == userId }

                // Fallback: si la caché no tiene datos, llamar directamente
                if (listas.isEmpty()) {
                    val listasResponse = safeApiCall { listaApiService.getListasByUsuarioId(userId) }
                    listas = when (listasResponse) {
                        is ApiResponse.Success -> listasResponse.data
                        is ApiResponse.Error -> {
                            _uiState.update { it.copy(isLoading = false, errorMessage = listasResponse.message) }
                            return@launch
                        }
                        is ApiResponse.Loading -> return@launch
                    }
                }

                // 2. Buscar la lista "Reviews"
                val listaReviews = listas.find { it.nombreLista.equals("Reviews", ignoreCase = true) }

                if (listaReviews == null || listaReviews.idLista == null) {
                    _uiState.update { it.copy(isLoading = false, reviews = emptyList()) }
                    return@launch
                }

                // 3. Obtener los libros de la lista Reviews
                val librosResponse = safeApiCall { libroListaApiService.getLibrosListaByListaId(listaReviews.idLista) }

                val librosLista = when (librosResponse) {
                    is ApiResponse.Success -> librosResponse.data
                    is ApiResponse.Error -> {
                        _uiState.update { it.copy(isLoading = false, errorMessage = librosResponse.message) }
                        return@launch
                    }
                    is ApiResponse.Loading -> {
                        return@launch
                    }
                }

                // 4. Filtrar solo los que tienen reseña
                val librosConResena = librosLista.filter { !it.resena.isNullOrBlank() }

                if (librosConResena.isEmpty()) {
                    _uiState.update { it.copy(isLoading = false, reviews = emptyList()) }
                    return@launch
                }

                // 5. Obtener detalles de cada libro en paralelo
                val reviews = librosConResena.map { libroLista ->
                    async {
                        val book = getCachedBook(libroLista.googleBookId)
                        Review(
                            id = libroLista.id ?: 0L,
                            googleBookId = libroLista.googleBookId,
                            bookTitle = book?.titulo ?: "Título no disponible",
                            bookAuthor = book?.autores?.joinToString(", ") ?: "Autor desconocido",
                            bookCoverUrl = book?.imagenPortada ?: book?.imagenPequena,
                            reviewText = libroLista.resena ?: "",
                            rating = libroLista.puntuacion ?: 0,
                            publishedDate = libroLista.fechaPublicacion
                        )
                    }
                }.awaitAll()

                _uiState.update { it.copy(isLoading = false, reviews = reviews) }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Error al cargar las reseñas") }
            }
        }
    }

    // Cachés con Deferred: si dos coroutines piden el mismo recurso,
    // la segunda espera al resultado de la primera en vez de hacer otra llamada HTTP
    private val bookCache = mutableMapOf<String, Deferred<GoogleBookDto?>>()
    private val bookCacheMutex = Mutex()
    private val userNameCacheMap = mutableMapOf<Long, Deferred<String>>()
    private val userCacheMutex = Mutex()

    private suspend fun getCachedBook(googleBookId: String): GoogleBookDto? {
        val deferred = bookCacheMutex.withLock {
            bookCache.getOrPut(googleBookId) {
                CompletableDeferred<GoogleBookDto?>().also { d ->
                    viewModelScope.launch {
                        val result = try {
                            val resp = safeApiCall { bookApiService.getBookById(googleBookId) }
                            when (resp) {
                                is ApiResponse.Success -> resp.data
                                else -> null
                            }
                        } catch (_: Exception) { null }
                        d.complete(result)
                    }
                }
            }
        }
        return deferred.await()
    }

    private suspend fun getCachedUserName(userId: Long): String {
        val deferred = userCacheMutex.withLock {
            userNameCacheMap.getOrPut(userId) {
                CompletableDeferred<String>().also { d ->
                    viewModelScope.launch {
                        val result = try {
                            val resp = safeApiCall { authApiService.getUsuarioById(userId) }
                            when (resp) {
                                is ApiResponse.Success -> resp.data.nombre
                                else -> "Usuario desconocido"
                            }
                        } catch (_: Exception) { "Usuario desconocido" }
                        d.complete(result)
                    }
                }
            }
        }
        return deferred.await()
    }

    private fun loadPublicReviews() {
        // Evitar cargas simultáneas que producen duplicados
        if (_uiState.value.isLoadingPublic) return

        val currentUserId = tokenManager.getUserId()

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingPublic = true, publicErrorMessage = null, publicReviews = emptyList()) }

            try {
                val allListas = getAllListasCached()
                processPublicListas(allListas, currentUserId)

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoadingPublic = false, publicErrorMessage = e.message ?: "Error al cargar reseñas públicas") }
            }
        }
    }

    private suspend fun processPublicListas(
        allListas: List<es.rafapuig.pmdm.compose.proyecto.data.remote.dto.ListaDto>,
        currentUserId: Long
    ) {
        // 2. Filtrar solo las listas "Reviews" de OTROS usuarios
        val reviewsListas = allListas.filter {
            it.nombreLista.equals("Reviews", ignoreCase = true) && it.idUsuario != currentUserId
        }

        if (reviewsListas.isEmpty()) {
            _uiState.update { it.copy(isLoadingPublic = false, publicReviews = emptyList()) }
            return
        }

        // 3. Channel para emitir reseñas progresivamente
        val channel = Channel<Review>(Channel.UNLIMITED)
        val netSemaphore = Semaphore(6)

        val collectorJob = viewModelScope.launch {
            for (review in channel) {
                _uiState.update { it.copy(publicReviews = it.publicReviews + review) }
            }
        }

        // 4. Procesar cada lista en paralelo
        val jobs = reviewsListas.map { lista ->
            viewModelScope.async {
                try {
                    val userNameDeferred = async { getCachedUserName(lista.idUsuario) }
                    val librosDeferred = async {
                        val resp = safeApiCall { libroListaApiService.getLibrosListaByListaId(lista.idLista!!) }
                        when (resp) {
                            is ApiResponse.Success -> resp.data
                            else -> emptyList()
                        }
                    }

                    val libros = librosDeferred.await()
                    val librosConResena = libros.filter { !it.resena.isNullOrBlank() }
                    if (librosConResena.isEmpty()) {
                        userNameDeferred.cancel()
                        return@async
                    }

                    val userName = userNameDeferred.await()

                    librosConResena.map { libroLista ->
                        async {
                            netSemaphore.withPermit {
                                val book = getCachedBook(libroLista.googleBookId)
                                val review = Review(
                                    id = libroLista.id ?: 0L,
                                    googleBookId = libroLista.googleBookId,
                                    bookTitle = book?.titulo ?: "Título no disponible",
                                    bookAuthor = book?.autores?.joinToString(", ") ?: "Autor desconocido",
                                    bookCoverUrl = book?.imagenPortada ?: book?.imagenPequena,
                                    reviewText = libroLista.resena ?: "",
                                    rating = libroLista.puntuacion ?: 0,
                                    publishedDate = libroLista.fechaPublicacion,
                                    userName = userName
                                )
                                channel.send(review)
                            }
                        }
                    }.awaitAll()
                } catch (_: Exception) { }
            }
        }

        jobs.awaitAll()
        channel.close()
        collectorJob.join()

        _uiState.update { it.copy(isLoadingPublic = false) }
    }

    private fun deleteReview(reviewId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val response = safeApiCallNoBody { libroListaApiService.deleteLibroLista(reviewId) }

                when (response) {
                    is ApiResponse.Success -> {
                        // Recargar las reseñas
                        loadReviews()
                    }
                    is ApiResponse.Error -> {
                        _uiState.update { it.copy(isLoading = false, errorMessage = response.message) }
                    }
                    is ApiResponse.Loading -> {}
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Error al eliminar la reseña") }
            }
        }
    }
}

