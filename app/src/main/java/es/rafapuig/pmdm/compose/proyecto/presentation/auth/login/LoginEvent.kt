package es.rafapuig.pmdm.compose.proyecto.presentation.auth.login

interface LoginEvent {
    data class LoginSuccess(val username: String) : LoginEvent
    object NavigateToRegister : LoginEvent
    data class ShowErrorMessage(val error: String) : LoginEvent
}