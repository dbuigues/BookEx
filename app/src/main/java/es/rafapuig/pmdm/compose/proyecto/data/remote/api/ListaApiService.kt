package es.rafapuig.pmdm.compose.proyecto.data.remote.api

import es.rafapuig.pmdm.compose.proyecto.data.remote.dto.ListaDto
import retrofit2.Response
import retrofit2.http.*

interface ListaApiService {

    @GET("api/listas")
    suspend fun getAllListas(): Response<List<ListaDto>>

    @GET("api/listas/{id}")
    suspend fun getListaById(@Path("id") id: Long): Response<ListaDto>

    @GET("api/listas/usuario/{idUsuario}")
    suspend fun getListasByUsuarioId(@Path("idUsuario") idUsuario: Long): Response<List<ListaDto>>

    @POST("api/listas")
    suspend fun createLista(@Body lista: ListaDto): Response<ListaDto>

    @PUT("api/listas/{id}")
    suspend fun updateLista(
        @Path("id") id: Long,
        @Body lista: ListaDto
    ): Response<ListaDto>

    @DELETE("api/listas/{id}")
    suspend fun deleteLista(@Path("id") id: Long): Response<Void>
}
