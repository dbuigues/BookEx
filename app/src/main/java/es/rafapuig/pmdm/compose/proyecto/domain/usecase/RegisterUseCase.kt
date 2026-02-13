package es.rafapuig.pmdm.compose.proyecto.domain.usecase

import es.rafapuig.pmdm.compose.proyecto.domain.AuthError
import es.rafapuig.pmdm.compose.proyecto.domain.AuthException
import es.rafapuig.pmdm.compose.proyecto.domain.repository.AuthRepository

class RegisterUseCase(private val repository: AuthRepository) {

    suspend fun execute(username: String, email: String, password: String): Boolean {

        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            throw AuthException(AuthError.EmptyFields)
        }

        return repository.register(username, email, password)
    }
}