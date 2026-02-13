package es.rafapuig.pmdm.compose.proyecto.data.remote.api

import es.rafapuig.pmdm.compose.proyecto.data.remote.dto.BookCreateRequest
import es.rafapuig.pmdm.compose.proyecto.data.remote.dto.BookDto
import retrofit2.Response
import retrofit2.http.*

interface BookApiService {

    @GET("api/books")
    suspend fun getAllBooks(): Response<List<BookDto>>

    @GET("api/books/{id}")
    suspend fun getBookById(@Path("id") id: Long): Response<BookDto>

    @GET("api/books/search")
    suspend fun searchBooks(@Query("query") query: String): Response<List<BookDto>>

    @GET("api/books/author/{author}")
    suspend fun getBooksByAuthor(@Path("author") author: String): Response<List<BookDto>>

    @GET("api/books/genre/{genre}")
    suspend fun getBooksByGenre(@Path("genre") genre: String): Response<List<BookDto>>

    @POST("api/books")
    suspend fun createBook(
        @Header("Authorization") token: String,
        @Body book: BookCreateRequest
    ): Response<BookDto>

    @PUT("api/books/{id}")
    suspend fun updateBook(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Body book: BookCreateRequest
    ): Response<BookDto>

    @DELETE("api/books/{id}")
    suspend fun deleteBook(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<Void>
}
