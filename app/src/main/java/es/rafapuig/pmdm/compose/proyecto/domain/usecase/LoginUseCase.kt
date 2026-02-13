package es.rafapuig.pmdm.compose.proyecto.domain.usecase

import es.rafapuig.pmdm.compose.proyecto.domain.AuthError
import es.rafapuig.pmdm.compose.proyecto.domain.AuthException
import es.rafapuig.pmdm.compose.proyecto.domain.model.User
import es.rafapuig.pmdm.compose.proyecto.domain.repository.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {

    suspend fun execute(email: String, password: String): Result<User> {

        if (email.isBlank() || password.isBlank()) {
            return Result.failure(AuthException(AuthError.EmptyFields))
        }

        if (!email.contains("@")) {
            return Result.failure(AuthException(AuthError.InvalidEmail))
        }

        return repository.login(email, password)

    }

}