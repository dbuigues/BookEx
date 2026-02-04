package es.rafapuig.pmdm.compose.proyecto.feature.auth.domain.usecase

import es.rafapuig.pmdm.compose.proyecto.feature.auth.domain.AuthError
import es.rafapuig.pmdm.compose.proyecto.feature.auth.domain.AuthException
import es.rafapuig.pmdm.compose.proyecto.feature.auth.domain.repository.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {


    suspend fun execute(email: String, password: String): Boolean {

        if(email.isBlank() || password.isBlank()) {
            throw AuthException(AuthError.EmptyFields)
        }

        // Lógica de autenticación simulada
        return repository.login(email, password)

    }

}