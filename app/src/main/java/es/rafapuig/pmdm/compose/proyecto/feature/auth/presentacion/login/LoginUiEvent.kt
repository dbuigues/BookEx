package es.rafapuig.pmdm.compose.proyecto.feature.auth.presentacion.login

interface LoginUiEvent {
    data class LoginSuccess(val username: String) : LoginUiEvent
    object NavigateToRegister : LoginUiEvent
    data class ShowErrorMessage(val error: String) : LoginUiEvent
}