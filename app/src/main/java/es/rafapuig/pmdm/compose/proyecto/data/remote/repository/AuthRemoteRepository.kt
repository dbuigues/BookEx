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
        return safeApiCall { authApiService.login(usuario) }
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
