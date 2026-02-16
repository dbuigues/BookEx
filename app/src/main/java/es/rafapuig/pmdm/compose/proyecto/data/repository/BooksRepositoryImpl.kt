package es.rafapuig.pmdm.compose.proyecto.data.repository

import es.rafapuig.pmdm.compose.proyecto.data.remote.ApiResponse
import es.rafapuig.pmdm.compose.proyecto.data.remote.api.BookApiService
import es.rafapuig.pmdm.compose.proyecto.data.remote.dto.GoogleBookDto
import es.rafapuig.pmdm.compose.proyecto.data.remote.safeApiCall
import es.rafapuig.pmdm.compose.proyecto.domain.model.Book
import es.rafapuig.pmdm.compose.proyecto.domain.repository.BooksRepository

/**
 * Implementación del repositorio de libros
 * Se comunica con la API remota para obtener datos de libros
 */
class BooksRepositoryImpl(
    private val bookApiService: BookApiService
) : BooksRepository {

    override suspend fun getPopularBooks(): Result<List<Book>> {
        // Buscar libros populares usando una búsqueda genérica
        // En el futuro esto podría ser un endpoint específico del backend
        return when (val response = safeApiCall {
            bookApiService.searchBooks("best sellers", maxResults = 10)
        }) {
            is ApiResponse.Success -> {
                val books = response.data.map { it.toDomain() }
                Result.success(books)
            }
            is ApiResponse.Error -> {
                Result.failure(Exception(response.message))
            }
            is ApiResponse.Loading -> {
                Result.failure(Exception("Loading"))
            }
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
