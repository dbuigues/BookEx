package es.rafapuig.pmdm.compose.proyecto.presentation.profile

sealed interface ProfileIntent {
    object LoadProfile : ProfileIntent
    object Logout : ProfileIntent
    object RefreshProfile : ProfileIntent
}
