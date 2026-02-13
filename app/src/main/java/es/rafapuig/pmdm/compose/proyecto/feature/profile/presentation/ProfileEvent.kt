package es.rafapuig.pmdm.compose.proyecto.feature.profile.presentation

sealed interface ProfileEvent {
    object LogoutSuccess : ProfileEvent
    data class ShowErrorMessage(val message: String) : ProfileEvent
}
