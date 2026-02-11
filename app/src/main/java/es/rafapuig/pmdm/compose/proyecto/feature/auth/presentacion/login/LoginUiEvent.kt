package es.rafapuig.pmdm.compose.proyecto.feature.auth.presentacion.login

interface LoginUiEvent {
    object LoginSuccess : LoginUiEvent
    object NavigateToRegister : LoginUiEvent
    data class ShowErrorMessage(val error: String) : LoginUiEvent
}