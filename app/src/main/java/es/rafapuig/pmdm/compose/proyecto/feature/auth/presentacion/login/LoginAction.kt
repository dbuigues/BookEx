package es.rafapuig.pmdm.compose.proyecto.feature.auth.presentacion.login

sealed interface LoginAction {
    data class OnLogin(val email: String, val password: String) : LoginAction
    object OnNavigateToRegister : LoginAction
}