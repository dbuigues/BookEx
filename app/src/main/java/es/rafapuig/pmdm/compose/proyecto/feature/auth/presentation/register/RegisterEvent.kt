package es.rafapuig.pmdm.compose.proyecto.feature.auth.presentation.register

interface RegisterEvent {
    object RegisterSuccess : RegisterEvent
    object NavigateToLogin : RegisterEvent
    data class ShowErrorMessage(val error: String) : RegisterEvent
}