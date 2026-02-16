package es.rafapuig.pmdm.compose.proyecto.presentation.auth.register

interface RegisterEvent {
    object RegisterSuccess : RegisterEvent
    object NavigateToLogin : RegisterEvent
    data class ShowErrorMessage(val error: String) : RegisterEvent
}