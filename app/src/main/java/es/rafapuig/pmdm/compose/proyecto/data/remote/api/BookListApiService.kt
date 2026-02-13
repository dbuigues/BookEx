package es.rafapuig.pmdm.compose.proyecto.data.remote.api

import es.rafapuig.pmdm.compose.proyecto.data.remote.dto.AddBookToListRequest
import es.rafapuig.pmdm.compose.proyecto.data.remote.dto.BookListCreateRequest
import es.rafapuig.pmdm.compose.proyecto.data.remote.dto.BookListDto
import retrofit2.Response
import retrofit2.http.*

interface BookListApiService {

    @GET("api/lists/user/{userId}")
    suspend fun getListsByUserId(@Path("userId") userId: Long): Response<List<BookListDto>>

    @GET("api/lists/{id}")
    suspend fun getListById(@Path("id") id: Long): Response<BookListDto>

    @GET("api/lists/public")
    suspend fun getPublicLists(): Response<List<BookListDto>>

    @POST("api/lists")
    suspend fun createList(
        @Header("Authorization") token: String,
        @Body list: BookListCreateRequest
    ): Response<BookListDto>

    @PUT("api/lists/{id}")
    suspend fun updateList(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Body list: BookListCreateRequest
    ): Response<BookListDto>

    @DELETE("api/lists/{id}")
    suspend fun deleteList(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<Void>

    @POST("api/lists/{listId}/books")
    suspend fun addBookToList(
        @Header("Authorization") token: String,
        @Path("listId") listId: Long,
        @Body request: AddBookToListRequest
    ): Response<BookListDto>

    @DELETE("api/lists/{listId}/books/{bookId}")
    suspend fun removeBookFromList(
        @Header("Authorization") token: String,
        @Path("listId") listId: Long,
        @Path("bookId") bookId: Long
    ): Response<Void>
}
