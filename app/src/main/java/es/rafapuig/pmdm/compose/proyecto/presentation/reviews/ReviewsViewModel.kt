package es.rafapuig.pmdm.compose.proyecto.presentation.reviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.rafapuig.pmdm.compose.proyecto.data.local.TokenManager
import es.rafapuig.pmdm.compose.proyecto.data.remote.ApiResponse
import es.rafapuig.pmdm.compose.proyecto.data.remote.api.BookApiService
import es.rafapuig.pmdm.compose.proyecto.data.remote.api.LibroListaApiService
import es.rafapuig.pmdm.compose.proyecto.data.remote.api.ListaApiService
import es.rafapuig.pmdm.compose.proyecto.data.remote.safeApiCall
import es.rafapuig.pmdm.compose.proyecto.domain.model.Review
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReviewsViewModel(
    private val tokenManager: TokenManager,
    private val listaApiService: ListaApiService,
    private val libroListaApiService: LibroListaApiService,
    private val bookApiService: BookApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewsState())
    val uiState = _uiState.asStateFlow()

    init {
        loadReviews()
    }

    fun onAction(action: ReviewsIntent) {
        when (action) {
            is ReviewsIntent.LoadReviews -> loadReviews()
            is ReviewsIntent.DeleteReview -> deleteReview(action.reviewId)
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
                // 1. Obtener las listas del usuario
                val listasResponse = safeApiCall { listaApiService.getListasByUsuarioId(userId) }

                val listas = when (listasResponse) {
                    is ApiResponse.Success -> listasResponse.data
                    is ApiResponse.Error -> {
                        _uiState.update { it.copy(isLoading = false, errorMessage = listasResponse.message) }
                        return@launch
                    }
                    is ApiResponse.Loading -> {
                        return@launch
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
                        try {
                            val bookResponse = safeApiCall { bookApiService.getBookById(libroLista.googleBookId) }

                            when (bookResponse) {
                                is ApiResponse.Success -> {
                                    val book = bookResponse.data
                                    Review(
                                        id = libroLista.id ?: 0L,
                                        googleBookId = libroLista.googleBookId,
                                        bookTitle = book.titulo,
                                        bookAuthor = book.autores?.joinToString(", ") ?: "Autor desconocido",
                                        bookCoverUrl = book.imagenPortada ?: book.imagenPequena,
                                        reviewText = libroLista.resena ?: "",
                                        rating = libroLista.puntuacion ?: 0,
                                        publishedDate = libroLista.fechaPublicacion
                                    )
                                }
                                else -> {
                                    // Si no se puede obtener el libro, usar datos básicos
                                    Review(
                                        id = libroLista.id ?: 0L,
                                        googleBookId = libroLista.googleBookId,
                                        bookTitle = "Título no disponible",
                                        bookAuthor = "Autor desconocido",
                                        bookCoverUrl = null,
                                        reviewText = libroLista.resena ?: "",
                                        rating = libroLista.puntuacion ?: 0,
                                        publishedDate = libroLista.fechaPublicacion
                                    )
                                }
                            }
                        } catch (e: Exception) {
                            Review(
                                id = libroLista.id ?: 0L,
                                googleBookId = libroLista.googleBookId,
                                bookTitle = "Título no disponible",
                                bookAuthor = "Autor desconocido",
                                bookCoverUrl = null,
                                reviewText = libroLista.resena ?: "",
                                rating = libroLista.puntuacion ?: 0,
                                publishedDate = libroLista.fechaPublicacion
                            )
                        }
                    }
                }.awaitAll()

                _uiState.update { it.copy(isLoading = false, reviews = reviews) }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Error al cargar las reseñas") }
            }
        }
    }

    private fun deleteReview(reviewId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val response = safeApiCall { libroListaApiService.deleteLibroLista(reviewId) }

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

