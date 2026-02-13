package es.rafapuig.pmdm.compose.proyecto.data.remote.repository

import es.rafapuig.pmdm.compose.proyecto.data.remote.ApiResponse
import es.rafapuig.pmdm.compose.proyecto.data.remote.api.AuthApiService
import es.rafapuig.pmdm.compose.proyecto.data.remote.dto.LoginRequest
import es.rafapuig.pmdm.compose.proyecto.data.remote.dto.LoginResponse
import es.rafapuig.pmdm.compose.proyecto.data.remote.dto.RegisterRequest
import es.rafapuig.pmdm.compose.proyecto.data.remote.dto.UserDto
import es.rafapuig.pmdm.compose.proyecto.data.remote.safeApiCall

class AuthRemoteRepository(private val authApiService: AuthApiService) {

    suspend fun register(username: String, email: String, password: String, profileImage: String? = null): ApiResponse<UserDto> {
        val request = RegisterRequest(username, email, password, profileImage)
        return safeApiCall { authApiService.register(request) }
    }

    suspend fun login(email: String, password: String): ApiResponse<LoginResponse> {
        val request = LoginRequest(email, password)
        return safeApiCall { authApiService.login(request) }
    }

    suspend fun getCurrentUser(token: String): ApiResponse<UserDto> {
        return safeApiCall { authApiService.getCurrentUser("Bearer $token") }
    }

    suspend fun updateProfile(token: String, user: UserDto): ApiResponse<UserDto> {
        return safeApiCall { authApiService.updateProfile("Bearer $token", user) }
    }
}
