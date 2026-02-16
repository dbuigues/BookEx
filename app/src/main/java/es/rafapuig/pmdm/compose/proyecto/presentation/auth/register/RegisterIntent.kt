package es.rafapuig.pmdm.compose.proyecto.presentation.auth.register

import android.net.Uri

sealed interface RegisterIntent {
    data class OnRegister(
        val username: String,
        val email: String,
        val password: String,
        val confirmPassword: String,
        ) : RegisterIntent
    object OnNavigateToLogin : RegisterIntent

    object OnSelectImage : RegisterIntent
    data class OnImageSelected(val uri: Uri) : RegisterIntent
    object OnUnselectImage : RegisterIntent
}


