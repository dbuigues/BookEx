package es.rafapuig.pmdm.compose.proyecto.data.remote.api

import es.rafapuig.pmdm.compose.proyecto.data.remote.dto.GoogleBookDto
import retrofit2.Response
import retrofit2.http.*

interface BookApiService {

    @GET("api/libros/{googleBookId}")
    suspend fun getBookById(@Path("googleBookId") googleBookId: String): Response<GoogleBookDto>

    @GET("api/libros/buscar")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int = 10
    ): Response<List<GoogleBookDto>>

    @GET("api/libros/buscar/titulo")
    suspend fun searchBooksByTitle(@Query("titulo") titulo: String): Response<List<GoogleBookDto>>

    @GET("api/libros/buscar/autor")
    suspend fun searchBooksByAuthor(@Query("autor") autor: String): Response<List<GoogleBookDto>>

    @GET("api/libros/buscar/categoria")
    suspend fun searchBooksByCategory(@Query("categoria") categoria: String): Response<List<GoogleBookDto>>

    @GET("api/libros/buscar/isbn/{isbn}")
    suspend fun searchBookByIsbn(@Path("isbn") isbn: String): Response<GoogleBookDto>
}
