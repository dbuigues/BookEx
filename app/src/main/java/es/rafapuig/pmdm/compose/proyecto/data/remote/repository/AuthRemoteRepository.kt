package es.rafapuig.pmdm.compose.proyecto.data.remote.repository

import es.rafapuig.pmdm.compose.proyecto.data.remote.ApiResponse
import es.rafapuig.pmdm.compose.proyecto.data.remote.api.AuthApiService
import es.rafapuig.pmdm.compose.proyecto.data.remote.dto.UsuarioDto
import es.rafapuig.pmdm.compose.proyecto.data.remote.safeApiCall

class AuthRemoteRepository(private val authApiService: AuthApiService) {

    suspend fun register(nombre: String, correo: String, contrasena: String, fotoPerfil: String? = null): ApiResponse<UsuarioDto> {
        val usuario = UsuarioDto(
            nombre = nombre,
            correo = correo,
            contrasena = contrasena,
            fotoPerfil = fotoPerfil
        )
        return safeApiCall { authApiService.register(usuario) }
    }

    suspend fun login(correo: String, contrasena: String): ApiResponse<UsuarioDto> {
        val usuario = UsuarioDto(
            nombre = "",
            correo = correo,
            contrasena = contrasena
        )

        return try {
            // Intentar login
            val response = authApiService.login(usuario)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.idUsuario != null && body.idUsuario != 0L) {
                    // El backend devuelve UsuarioDTO completo
                    ApiResponse.Success(body)
                } else {
                    // El backend puede haber devuelto solo el correo o datos incompletos
                    // Obtener datos completos del usuario
                    getUsuarioByCorreo(correo)
                }
            } else if (response.code() == 401) {
                ApiResponse.Error("Credenciales incorrectas", 401)
            } else {
                ApiResponse.Error(
                    message = response.errorBody()?.string() ?: "Error de autenticación",
                    code = response.code()
                )
            }
        } catch (e: Exception) {
            // Si hay error de parsing (el backend devuelve String en lugar de UsuarioDTO)
            // significa que el login fue exitoso pero con formato antiguo
            if (e.message?.contains("Expected", ignoreCase = true) == true ||
                e.message?.contains("BEGIN_OBJECT", ignoreCase = true) == true) {
                // Login exitoso, obtener datos del usuario
                getUsuarioByCorreo(correo)
            } else {
                ApiResponse.Error(e.message ?: "Error de red")
            }
        }
    }

    suspend fun getUsuarioById(id: Long): ApiResponse<UsuarioDto> {
        return safeApiCall { authApiService.getUsuarioById(id) }
    }

    suspend fun getUsuarioByCorreo(correo: String): ApiResponse<UsuarioDto> {
        return safeApiCall { authApiService.getUsuarioByCorreo(correo) }
    }

    suspend fun updateUsuario(id: Long, usuario: UsuarioDto): ApiResponse<UsuarioDto> {
        return safeApiCall { authApiService.updateUsuario(id, usuario) }
    }
}
