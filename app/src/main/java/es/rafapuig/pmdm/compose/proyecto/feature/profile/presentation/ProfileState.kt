package es.rafapuig.pmdm.compose.proyecto.feature.profile.presentation

import es.rafapuig.pmdm.compose.proyecto.domain.model.User

data class ProfileState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
