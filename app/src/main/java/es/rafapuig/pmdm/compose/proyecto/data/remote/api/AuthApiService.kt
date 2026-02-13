package es.rafapuig.pmdm.compose.proyecto.data.remote.api

import es.rafapuig.pmdm.compose.proyecto.data.remote.dto.LoginRequest
import es.rafapuig.pmdm.compose.proyecto.data.remote.dto.LoginResponse
import es.rafapuig.pmdm.compose.proyecto.data.remote.dto.RegisterRequest
import es.rafapuig.pmdm.compose.proyecto.data.remote.dto.UserDto
import retrofit2.Response
import retrofit2.http.*

interface AuthApiService {

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<UserDto>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("api/auth/me")
    suspend fun getCurrentUser(@Header("Authorization") token: String): Response<UserDto>

    @PUT("api/auth/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body user: UserDto
    ): Response<UserDto>
}
