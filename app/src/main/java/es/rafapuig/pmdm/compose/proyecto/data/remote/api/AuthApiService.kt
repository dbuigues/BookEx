package es.rafapuig.pmdm.compose.proyecto.data.remote.api

import es.rafapuig.pmdm.compose.proyecto.data.remote.dto.UsuarioDto
import retrofit2.Response
import retrofit2.http.*

interface AuthApiService {

    @POST("api/usuarios")
    suspend fun register(@Body usuario: UsuarioDto): Response<UsuarioDto>

    @POST("api/usuarios/login")
    suspend fun login(@Body usuario: UsuarioDto): Response<UsuarioDto>

    @GET("api/usuarios/{id}")
    suspend fun getUsuarioById(@Path("id") id: Long): Response<UsuarioDto>

    @GET("api/usuarios/correo/{correo}")
    suspend fun getUsuarioByCorreo(@Path("correo") correo: String): Response<UsuarioDto>

    @GET("api/usuarios/getpfp/{correo}")
    suspend fun getProfilePicture(@Path("correo") correo: String): Response<List<String>>

    @PUT("api/usuarios/{id}")
    suspend fun updateUsuario(
        @Path("id") id: Long,
        @Body usuario: UsuarioDto
    ): Response<UsuarioDto>

    @DELETE("api/usuarios/{id}")
    suspend fun deleteUsuario(@Path("id") id: Long): Response<Void>

    @GET("api/usuarios")
    suspend fun getAllUsuarios(): Response<List<UsuarioDto>>
}
