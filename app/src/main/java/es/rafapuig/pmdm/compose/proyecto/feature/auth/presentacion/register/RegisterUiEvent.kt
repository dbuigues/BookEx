package es.rafapuig.pmdm.compose.proyecto.feature.auth.presentacion.register

interface RegisterUiEvent {
    object RegisterSuccess : RegisterUiEvent
    object NavigateToLogin : RegisterUiEvent
    data class ShowErrorMessage(val error: String) : RegisterUiEvent
}