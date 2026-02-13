package es.rafapuig.pmdm.compose.proyecto.domain.usecase

import es.rafapuig.pmdm.compose.proyecto.domain.AuthError
import es.rafapuig.pmdm.compose.proyecto.domain.AuthException
import es.rafapuig.pmdm.compose.proyecto.domain.model.User
import es.rafapuig.pmdm.compose.proyecto.domain.repository.AuthRepository

class RegisterUseCase(private val repository: AuthRepository) {

    suspend fun execute(username: String, email: String, password: String): Result<User> {

        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            return Result.failure(AuthException(AuthError.EmptyFields))
        }

        if (!email.contains("@")) {
            return Result.failure(AuthException(AuthError.InvalidEmail))
        }

        if (password.length < 6) {
            return Result.failure(AuthException(AuthError.WeakPassword))
        }

        return repository.register(username, email, password)
    }
}