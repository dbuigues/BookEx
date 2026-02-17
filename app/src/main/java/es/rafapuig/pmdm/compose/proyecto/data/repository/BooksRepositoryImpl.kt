package es.rafapuig.pmdm.compose.proyecto.data.repository

import es.rafapuig.pmdm.compose.proyecto.data.remote.ApiResponse
import es.rafapuig.pmdm.compose.proyecto.data.remote.api.BookApiService
import es.rafapuig.pmdm.compose.proyecto.data.remote.dto.GoogleBookDto
import es.rafapuig.pmdm.compose.proyecto.data.remote.safeApiCall
import es.rafapuig.pmdm.compose.proyecto.domain.model.Book
import es.rafapuig.pmdm.compose.proyecto.domain.repository.BooksRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

/**
 * Implementación del repositorio de libros
 * Se comunica con la API remota para obtener datos de libros
 */
class BooksRepositoryImpl(
    private val bookApiService: BookApiService
) : BooksRepository {

    override suspend fun getPopularBooks(): Result<List<Book>> = coroutineScope {
        val popularTitles = listOf(
            "1984",
            "El gran Gatsby",
            "Te di ojos y miraste las tinieblas",
            "Rebelión en la granja",
            "Crónica de una muerte anunciada",
            "El viejo y el mar",
            "El extranjero Camus",
            "Los papeles de aspern Henry James"
        )

        // Buscar todos los libros en paralelo
        val deferredBooks = popularTitles.map { title ->
            async {
                try {
                    when (val response = safeApiCall {
                        bookApiService.searchBooksByTitle(titulo = title)
                    }) {
                        is ApiResponse.Success -> {
                            response.data.firstOrNull()?.toDomain()
                        }
                        is ApiResponse.Error -> null
                        is ApiResponse.Loading -> null
                    }
                } catch (e: Exception) {
                    null
                }
            }
        }

        val books = deferredBooks.awaitAll().filterNotNull()

        return@coroutineScope if (books.isNotEmpty()) {
            Result.success(books)
        } else {
            Result.failure(Exception("No se pudieron cargar libros populares"))
        }
    }

    override suspend fun searchBooks(query: String, maxResults: Int): Result<List<Book>> {
        if (query.isBlank()) return Result.success(emptyList())

        return when (val response = safeApiCall {
            bookApiService.searchBooks(query = query, maxResults = maxResults)
        }) {
            is ApiResponse.Success -> Result.success(response.data.map { it.toDomain() })
            is ApiResponse.Error -> Result.failure(Exception(response.message))
            is ApiResponse.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun searchBooksByTitle(title: String): Result<List<Book>> {
        if (title.isBlank()) return Result.success(emptyList())

        return when (val response = safeApiCall {
            bookApiService.searchBooksByTitle(titulo = title)
        }) {
            is ApiResponse.Success -> Result.success(response.data.map { it.toDomain() })
            is ApiResponse.Error -> Result.failure(Exception(response.message))
            is ApiResponse.Loading -> Result.failure(Exception("Loading"))
        }
    }
}

/**
 * Extensión para mapear GoogleBookDto a Book del dominio
 */
private fun GoogleBookDto.toDomain(): Book {
    return Book(
        id = googleBookId,
        title = titulo,
        author = autores?.joinToString(", ") ?: "Autor desconocido",
        description = descripcion ?: "Sin descripción disponible",
        coverUrl = imagenPortada ?: imagenPequena,
        isbn = isbn
    )
}
