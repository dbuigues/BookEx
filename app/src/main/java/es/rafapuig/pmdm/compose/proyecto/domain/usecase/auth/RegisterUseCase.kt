package es.rafapuig.pmdm.compose.proyecto.domain.usecase.auth

import es.rafapuig.pmdm.compose.proyecto.domain.AuthError
import es.rafapuig.pmdm.compose.proyecto.domain.AuthException
import es.rafapuig.pmdm.compose.proyecto.domain.model.User
import es.rafapuig.pmdm.compose.proyecto.domain.repository.AuthRepository
import es.rafapuig.pmdm.compose.proyecto.domain.repository.ListsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class RegisterUseCase(
    private val repository: AuthRepository,
    private val listsRepository: ListsRepository
) {

    suspend fun execute(
        username: String,
        email: String,
        password: String,
        profileImageBase64: String? = null
    ): Result<User> {

        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            return Result.failure(AuthException(AuthError.EmptyFields))
        }

        if (!email.contains("@")) {
            return Result.failure(AuthException(AuthError.InvalidEmail))
        }

        if (password.length < 6) {
            return Result.failure(AuthException(AuthError.WeakPassword))
        }

        val result = repository.register(username, email, password, profileImageBase64)

        // Si el registro fue exitoso, crear las listas por defecto
        result.onSuccess { user ->
            createDefaultLists(user.id)
        }

        return result
    }

    private suspend fun createDefaultLists(userId: Long) {
        try {
            // Crear ambas listas en paralelo
            coroutineScope {
                val favoritosDeferred = async {
                    try {
                        listsRepository.createList(userId, "Favoritos")
                    } catch (e: Exception) {
                        // Ignorar errores si la lista ya existe
                        null
                    }
                }
                val reviewsDeferred = async {
                    try {
                        listsRepository.createList(userId, "Reviews")
                    } catch (e: Exception) {
                        // Ignorar errores si la lista ya existe
                        null
                    }
                }
                favoritosDeferred.await()
                reviewsDeferred.await()
            }
        } catch (e: Exception) {
            // No fallar el registro si no se pueden crear las listas
            e.printStackTrace()
        }
    }
}