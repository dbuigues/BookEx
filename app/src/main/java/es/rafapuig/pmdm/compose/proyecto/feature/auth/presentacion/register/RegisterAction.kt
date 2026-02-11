package es.rafapuig.pmdm.compose.proyecto.feature.auth.presentacion.register

import android.net.Uri

sealed interface RegisterAction {
    data class OnRegister(
        val username: String,
        val email: String,
        val password: String,
        ) : RegisterAction
    object OnNavigateToLogin : RegisterAction

    object OnSelectImage : RegisterAction
    data class OnImageSelected(val uri: Uri) : RegisterAction
    object OnUnselectImage : RegisterAction
}


