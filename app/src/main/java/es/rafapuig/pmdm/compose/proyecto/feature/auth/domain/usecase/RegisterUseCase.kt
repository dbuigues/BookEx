package es.rafapuig.pmdm.compose.proyecto.feature.auth.domain.usecase

import es.rafapuig.pmdm.compose.proyecto.feature.auth.domain.AuthError
import es.rafapuig.pmdm.compose.proyecto.feature.auth.domain.AuthException
import es.rafapuig.pmdm.compose.proyecto.feature.auth.domain.repository.AuthRepository

class RegisterUseCase(private val repository: AuthRepository) {

    suspend fun execute(username: String, email: String, password: String): Boolean {

        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            throw AuthException(AuthError.EmptyFields)
        }

        return repository.register(username, email, password)
    }
}