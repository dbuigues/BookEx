package es.rafapuig.pmdm.compose.proyecto.feature.auth.data.repository

import es.rafapuig.pmdm.compose.proyecto.feature.auth.domain.repository.AuthRepository

class AuthRepositoryImpl : AuthRepository {

    private var userLoggedIn = false

    override suspend fun login(email: String, password: String): Boolean {
        if (email == "test@test.com" && password == "12345") {
            userLoggedIn = true
            return true
        }
        return false
    }

    override suspend fun register(username: String, email: String, password: String): Boolean {
        //Here you would typically add logic to register the user in your backend or database
        //For this example, we'll just simulate a successful registration
        userLoggedIn = true
        return true
    }

    override suspend fun logout(): Boolean {
        userLoggedIn = false
        return true
    }

    override suspend fun isUserLoggedIn(): Boolean {
        return userLoggedIn
    }
}