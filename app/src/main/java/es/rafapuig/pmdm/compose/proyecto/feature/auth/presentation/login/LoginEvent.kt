package es.rafapuig.pmdm.compose.proyecto.feature.auth.presentation.login

interface LoginEvent {
    data class LoginSuccess(val username: String) : LoginEvent
    object NavigateToRegister : LoginEvent
    data class ShowErrorMessage(val error: String) : LoginEvent
}