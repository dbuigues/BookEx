package es.rafapuig.pmdm.compose.proyecto.feature.profile.presentation

sealed interface ProfileIntent {
    object LoadProfile : ProfileIntent
    object Logout : ProfileIntent
    object RefreshProfile : ProfileIntent
}
