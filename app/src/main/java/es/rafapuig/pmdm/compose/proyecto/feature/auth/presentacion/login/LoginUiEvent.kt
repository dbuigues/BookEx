package es.rafapuig.pmdm.compose.proyecto.feature.auth.presentacion.login

import es.rafapuig.pmdm.compose.proyecto.feature.auth.domain.AuthError

interface LoginUiEvent {
    object LoginSuccess : LoginUiEvent
    object NavigateToRegister : LoginUiEvent
    data class ShowErrorMessage(val error : AuthError) : LoginUiEvent
}