package es.rafapuig.pmdm.compose.proyecto.data.repository

import es.rafapuig.pmdm.compose.proyecto.data.local.TokenManager
import es.rafapuig.pmdm.compose.proyecto.data.remote.ApiResponse
import es.rafapuig.pmdm.compose.proyecto.data.remote.repository.AuthRemoteRepository
import es.rafapuig.pmdm.compose.proyecto.domain.model.User
import es.rafapuig.pmdm.compose.proyecto.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val authRemoteRepository: AuthRemoteRepository,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        return when (val response = authRemoteRepository.login(email, password)) {
            is ApiResponse.Success -> {
                val usuarioDto = response.data
                // Guardar datos del usuario
                usuarioDto.idUsuario?.let { tokenManager.saveUserId(it) }
                tokenManager.saveUsername(usuarioDto.nombre)
                tokenManager.saveUserEmail(usuarioDto.correo)

                Result.success(
                    User(
                        id = usuarioDto.idUsuario ?: 0L,
                        username = usuarioDto.nombre,
                        email = usuarioDto.correo,
                        profileImage = usuarioDto.fotoPerfil
                    )
                )
            }
            is ApiResponse.Error -> {
                Result.failure(Exception(response.message))
            }
            is ApiResponse.Loading -> {
                Result.failure(Exception("Loading"))
            }
        }
    }

    override suspend fun register(username: String, email: String, password: String): Result<User> {
        return when (val response = authRemoteRepository.register(username, email, password)) {
            is ApiResponse.Success -> {
                val usuarioDto = response.data
                Result.success(
                    User(
                        id = usuarioDto.idUsuario ?: 0L,
                        username = usuarioDto.nombre,
                        email = usuarioDto.correo,
                        profileImage = usuarioDto.fotoPerfil
                    )
                )
            }
            is ApiResponse.Error -> {
                Result.failure(Exception(response.message))
            }
            is ApiResponse.Loading -> {
                Result.failure(Exception("Loading"))
            }
        }
    }

    override suspend fun logout() {
        tokenManager.clearAll()
    }

    override suspend fun getCurrentUser(): User? {
        val userId = tokenManager.getUserId()
        if (userId == -1L) return null

        return when (val response = authRemoteRepository.getUsuarioById(userId)) {
            is ApiResponse.Success -> {
                val usuarioDto = response.data
                User(
                    id = usuarioDto.idUsuario ?: 0L,
                    username = usuarioDto.nombre,
                    email = usuarioDto.correo,
                    profileImage = usuarioDto.fotoPerfil
                )
            }
            is ApiResponse.Error -> {
                // Si hay error, intentar con datos locales
                val username = tokenManager.getUsername()
                val email = tokenManager.getUserEmail()

                if (username != null && email != null) {
                    User(
                        id = userId,
                        username = username,
                        email = email,
                        profileImage = null
                    )
                } else {
                    null
                }
            }
            is ApiResponse.Loading -> null
        }
    }

    override suspend fun isUserLoggedIn(): Boolean {
        return tokenManager.getUserId() != -1L
    }
}