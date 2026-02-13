package es.rafapuig.pmdm.compose.proyecto.data.remote.api

import es.rafapuig.pmdm.compose.proyecto.data.remote.dto.LibroListaDto
import retrofit2.Response
import retrofit2.http.*

interface LibroListaApiService {

    @GET("api/libros-listas")
    suspend fun getAllLibrosListas(): Response<List<LibroListaDto>>

    @GET("api/libros-listas/{id}")
    suspend fun getLibroListaById(@Path("id") id: Long): Response<LibroListaDto>

    @GET("api/libros-listas/lista/{idLista}")
    suspend fun getLibrosListaByListaId(@Path("idLista") idLista: Long): Response<List<LibroListaDto>>

    @GET("api/libros-listas/puntuacion/{puntuacion}")
    suspend fun getLibrosListaByPuntuacion(@Path("puntuacion") puntuacion: Int): Response<List<LibroListaDto>>

    @POST("api/libros-listas")
    suspend fun createLibroLista(@Body libroLista: LibroListaDto): Response<LibroListaDto>

    @PUT("api/libros-listas/{id}")
    suspend fun updateLibroLista(
        @Path("id") id: Long,
        @Body libroLista: LibroListaDto
    ): Response<LibroListaDto>

    @DELETE("api/libros-listas/{id}")
    suspend fun deleteLibroLista(@Path("id") id: Long): Response<Void>
}
