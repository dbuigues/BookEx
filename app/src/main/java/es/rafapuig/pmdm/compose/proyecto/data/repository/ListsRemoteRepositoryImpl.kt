package es.rafapuig.pmdm.compose.proyecto.data.repository

import es.rafapuig.pmdm.compose.proyecto.data.remote.ApiResponse
import es.rafapuig.pmdm.compose.proyecto.data.remote.safeApiCall
import es.rafapuig.pmdm.compose.proyecto.data.remote.api.BookApiService
import es.rafapuig.pmdm.compose.proyecto.data.remote.api.ListaApiService
import es.rafapuig.pmdm.compose.proyecto.data.remote.api.LibroListaApiService
import es.rafapuig.pmdm.compose.proyecto.data.remote.dto.ListaDto
import es.rafapuig.pmdm.compose.proyecto.data.remote.dto.LibroListaDto
import es.rafapuig.pmdm.compose.proyecto.domain.model.BookList
import es.rafapuig.pmdm.compose.proyecto.domain.model.ListBook
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class ListsRemoteRepositoryImpl(
    private val listaApiService: ListaApiService,
    private val libroListaApiService: LibroListaApiService,
    private val bookApiService: BookApiService? = null
) : es.rafapuig.pmdm.compose.proyecto.domain.repository.ListsRepository {

    private fun dtoToDomain(dto: ListaDto): BookList {
        return BookList(
            id = dto.idLista?.toString() ?: "",
            name = dto.nombreLista,
            ownerId = dto.idUsuario,
            createdAt = "",
            items = mutableListOf()
        )
    }

    override suspend fun getListsForOwner(ownerId: Long): List<BookList> {
        return when (val resp = safeApiCall { listaApiService.getListasByUsuarioId(ownerId) }) {
            is ApiResponse.Success -> resp.data.map { dtoToDomain(it) }
            is ApiResponse.Error -> throw Exception(resp.message)
            is ApiResponse.Loading -> emptyList()
        }
    }

    override suspend fun createList(ownerId: Long, name: String): BookList {
        val payload = ListaDto(idLista = null, idUsuario = ownerId, nombreLista = name)
        return when (val resp = safeApiCall { listaApiService.createLista(payload) }) {
            is ApiResponse.Success -> dtoToDomain(resp.data)
            is ApiResponse.Error -> throw Exception(resp.message)
            is ApiResponse.Loading -> throw Exception("Loading")
        }
    }

    override suspend fun deleteList(ownerId: Long, listId: String): Boolean {
        val idLong = try { listId.toLong() } catch (_: Exception) { return false }
        return when (safeApiCall { listaApiService.deleteLista(idLong) }) {
            is ApiResponse.Success -> true
            is ApiResponse.Error -> false
            is ApiResponse.Loading -> false
        }
    }

    override suspend fun addBookToList(ownerId: Long, listId: String, bookId: String): Boolean {
        val idLong = try { listId.toLong() } catch (_: Exception) { return false }
        val payload = LibroListaDto(id = null, idLista = idLong, googleBookId = bookId)
        return when (safeApiCall { libroListaApiService.createLibroLista(payload) }) {
            is ApiResponse.Success -> true
            is ApiResponse.Error -> false
            is ApiResponse.Loading -> false
        }
    }

    override suspend fun getBooksInList(listId: String): List<ListBook> {
        val idLong = try { listId.toLong() } catch (_: Exception) { return emptyList() }

        // Obtener los libros de la lista
        val librosResponse = when (val resp = safeApiCall { libroListaApiService.getLibrosListaByListaId(idLong) }) {
            is ApiResponse.Success -> resp.data
            is ApiResponse.Error -> throw Exception(resp.message)
            is ApiResponse.Loading -> return emptyList()
        }

        if (librosResponse.isEmpty()) return emptyList()

        // Si tenemos el servicio de libros, obtener detalles
        if (bookApiService != null) {
            return coroutineScope {
                librosResponse.map { libroLista ->
                    async {
                        try {
                            val bookResponse = safeApiCall { bookApiService.getBookById(libroLista.googleBookId) }
                            when (bookResponse) {
                                is ApiResponse.Success -> {
                                    val book = bookResponse.data
                                    ListBook(
                                        id = libroLista.id ?: 0L,
                                        googleBookId = libroLista.googleBookId,
                                        title = book.titulo,
                                        author = book.autores?.joinToString(", ") ?: "Autor desconocido",
                                        coverUrl = book.imagenPortada ?: book.imagenPequena,
                                        review = libroLista.resena,
                                        rating = libroLista.puntuacion
                                    )
                                }
                                else -> {
                                    ListBook(
                                        id = libroLista.id ?: 0L,
                                        googleBookId = libroLista.googleBookId,
                                        title = "Título no disponible",
                                        author = "Autor desconocido",
                                        coverUrl = null,
                                        review = libroLista.resena,
                                        rating = libroLista.puntuacion
                                    )
                                }
                            }
                        } catch (e: Exception) {
                            ListBook(
                                id = libroLista.id ?: 0L,
                                googleBookId = libroLista.googleBookId,
                                title = "Título no disponible",
                                author = "Autor desconocido",
                                coverUrl = null,
                                review = libroLista.resena,
                                rating = libroLista.puntuacion
                            )
                        }
                    }
                }.awaitAll()
            }
        } else {
            // Sin servicio de libros, devolver datos básicos
            return librosResponse.map { libroLista ->
                ListBook(
                    id = libroLista.id ?: 0L,
                    googleBookId = libroLista.googleBookId,
                    title = libroLista.googleBookId,
                    author = "Autor desconocido",
                    coverUrl = null,
                    review = libroLista.resena,
                    rating = libroLista.puntuacion
                )
            }
        }
    }

    override suspend fun removeBookFromList(bookListId: Long): Boolean {
        return when (safeApiCall { libroListaApiService.deleteLibroLista(bookListId) }) {
            is ApiResponse.Success -> true
            is ApiResponse.Error -> false
            is ApiResponse.Loading -> false
        }
    }

    override suspend fun addBookWithReview(
        ownerId: Long,
        listId: String,
        bookId: String,
        review: String,
        rating: Int
    ): Boolean {
        val idLong = try { listId.toLong() } catch (_: Exception) { return false }
        val payload = LibroListaDto(
            id = null,
            idLista = idLong,
            googleBookId = bookId,
            resena = review,
            puntuacion = rating,
            fechaPublicacion = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                .format(java.util.Date())
        )
        return when (safeApiCall { libroListaApiService.createLibroLista(payload) }) {
            is ApiResponse.Success -> true
            is ApiResponse.Error -> false
            is ApiResponse.Loading -> false
        }
    }

    override suspend fun getReviewsListId(ownerId: Long): String? {
        val lists = getListsForOwner(ownerId)
        return lists.find { it.name.equals("Reviews", ignoreCase = true) }?.id
    }
}
