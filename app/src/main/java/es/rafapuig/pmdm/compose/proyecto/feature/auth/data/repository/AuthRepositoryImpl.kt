package es.rafapuig.pmdm.compose.proyecto.feature.auth.data.repository

import es.rafapuig.pmdm.compose.proyecto.feature.auth.domain.repository.AuthRepository

class AuthRepositoryImpl : AuthRepository {

    override suspend fun login(email: String, password: String): Boolean {
        return email == "test@test.com " && password == "12345"
    }

    override suspend fun register(email: String, password: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun logout(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun isUserLoggedIn(): Boolean {
        TODO("Not yet implemented")
    }
}