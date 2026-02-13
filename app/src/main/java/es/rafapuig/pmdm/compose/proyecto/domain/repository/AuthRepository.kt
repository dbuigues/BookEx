package es.rafapuig.pmdm.compose.proyecto.domain.repository

import es.rafapuig.pmdm.compose.proyecto.domain.model.User

interface AuthRepository {

    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(username: String, email: String, password: String): Result<User>
    suspend fun logout()
    suspend fun getCurrentUser(): User?
    suspend fun isUserLoggedIn(): Boolean

}