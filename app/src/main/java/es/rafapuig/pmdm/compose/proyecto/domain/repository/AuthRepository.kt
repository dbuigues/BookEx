package es.rafapuig.pmdm.compose.proyecto.domain.repository

interface AuthRepository {

    suspend fun login(email: String, password: String): Boolean
    suspend fun register(username: String, email: String, password: String): Boolean
    suspend fun logout(): Boolean
    suspend fun isUserLoggedIn(): Boolean

}