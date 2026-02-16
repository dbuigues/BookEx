package es.rafapuig.pmdm.compose.proyecto.presentation.auth.login

sealed interface LoginIntent {
    data class OnLogin(val email: String, val password: String) : LoginIntent
    object OnNavigateToRegister : LoginIntent
}