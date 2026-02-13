package es.rafapuig.pmdm.compose.proyecto.data.remote.repository

import es.rafapuig.pmdm.compose.proyecto.data.remote.ApiResponse
import es.rafapuig.pmdm.compose.proyecto.data.remote.api.BookListApiService
import es.rafapuig.pmdm.compose.proyecto.data.remote.dto.AddBookToListRequest
import es.rafapuig.pmdm.compose.proyecto.data.remote.dto.BookListCreateRequest
import es.rafapuig.pmdm.compose.proyecto.data.remote.dto.BookListDto
import es.rafapuig.pmdm.compose.proyecto.data.remote.safeApiCall

class BookListRemoteRepository(private val bookListApiService: BookListApiService) {

    suspend fun getListsByUserId(userId: Long): ApiResponse<List<BookListDto>> {
        return safeApiCall { bookListApiService.getListsByUserId(userId) }
    }

    suspend fun getListById(id: Long): ApiResponse<BookListDto> {
        return safeApiCall { bookListApiService.getListById(id) }
    }

    suspend fun getPublicLists(): ApiResponse<List<BookListDto>> {
        return safeApiCall { bookListApiService.getPublicLists() }
    }

    suspend fun createList(token: String, name: String, description: String?, isPublic: Boolean): ApiResponse<BookListDto> {
        val request = BookListCreateRequest(name, description, isPublic)
        return safeApiCall { bookListApiService.createList("Bearer $token", request) }
    }

    suspend fun updateList(token: String, id: Long, name: String, description: String?, isPublic: Boolean): ApiResponse<BookListDto> {
        val request = BookListCreateRequest(name, description, isPublic)
        return safeApiCall { bookListApiService.updateList("Bearer $token", id, request) }
    }

    suspend fun deleteList(token: String, id: Long): ApiResponse<Void> {
        return safeApiCall { bookListApiService.deleteList("Bearer $token", id) }
    }

    suspend fun addBookToList(token: String, listId: Long, bookId: Long): ApiResponse<BookListDto> {
        val request = AddBookToListRequest(bookId)
        return safeApiCall { bookListApiService.addBookToList("Bearer $token", listId, request) }
    }

    suspend fun removeBookFromList(token: String, listId: Long, bookId: Long): ApiResponse<Void> {
        return safeApiCall { bookListApiService.removeBookFromList("Bearer $token", listId, bookId) }
    }
}
