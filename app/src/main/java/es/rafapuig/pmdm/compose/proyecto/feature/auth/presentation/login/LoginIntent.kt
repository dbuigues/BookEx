package es.rafapuig.pmdm.compose.proyecto.feature.auth.presentation.login

sealed interface LoginIntent {
    data class OnLogin(val email: String, val password: String) : LoginIntent
    object OnNavigateToRegister : LoginIntent
}