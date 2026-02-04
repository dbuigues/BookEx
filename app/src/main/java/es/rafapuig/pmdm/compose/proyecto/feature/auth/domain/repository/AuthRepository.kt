package es.rafapuig.pmdm.compose.proyecto.feature.auth.domain.repository

interface AuthRepository {

    suspend fun login(email: String, password: String): Boolean
    suspend fun register(email: String, password: String): Boolean
    suspend fun logout(): Boolean
    suspend fun isUserLoggedIn(): Boolean

}