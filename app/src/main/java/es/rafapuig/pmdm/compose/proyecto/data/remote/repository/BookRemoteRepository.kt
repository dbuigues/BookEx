package es.rafapuig.pmdm.compose.proyecto.data.remote.repository

import es.rafapuig.pmdm.compose.proyecto.data.remote.ApiResponse
import es.rafapuig.pmdm.compose.proyecto.data.remote.api.BookApiService
import es.rafapuig.pmdm.compose.proyecto.data.remote.dto.BookCreateRequest
import es.rafapuig.pmdm.compose.proyecto.data.remote.dto.BookDto
import es.rafapuig.pmdm.compose.proyecto.data.remote.safeApiCall

class BookRemoteRepository(private val bookApiService: BookApiService) {

    suspend fun getAllBooks(): ApiResponse<List<BookDto>> {
        return safeApiCall { bookApiService.getAllBooks() }
    }

    suspend fun getBookById(id: Long): ApiResponse<BookDto> {
        return safeApiCall { bookApiService.getBookById(id) }
    }

    suspend fun searchBooks(query: String): ApiResponse<List<BookDto>> {
        return safeApiCall { bookApiService.searchBooks(query) }
    }

    suspend fun getBooksByAuthor(author: String): ApiResponse<List<BookDto>> {
        return safeApiCall { bookApiService.getBooksByAuthor(author) }
    }

    suspend fun getBooksByGenre(genre: String): ApiResponse<List<BookDto>> {
        return safeApiCall { bookApiService.getBooksByGenre(genre) }
    }

    suspend fun createBook(token: String, book: BookCreateRequest): ApiResponse<BookDto> {
        return safeApiCall { bookApiService.createBook("Bearer $token", book) }
    }

    suspend fun updateBook(token: String, id: Long, book: BookCreateRequest): ApiResponse<BookDto> {
        return safeApiCall { bookApiService.updateBook("Bearer $token", id, book) }
    }

    suspend fun deleteBook(token: String, id: Long): ApiResponse<Void> {
        return safeApiCall { bookApiService.deleteBook("Bearer $token", id) }
    }
}
