package es.rafapuig.pmdm.compose.proyecto.presentation.profile

sealed interface ProfileEvent {
    object LogoutSuccess : ProfileEvent
    data class ShowErrorMessage(val message: String) : ProfileEvent
}
