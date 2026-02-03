package es.rafapuig.pmdm.compose.proyecto.feature.auth.intent

import android.net.Uri

/**
 * Intent (Acciones) para la pantalla de autenticación
 * Siguiendo el patrón MVI, estos son los Intents que representan las acciones del usuario
 */
sealed class AuthIntent {

    // Intents de navegación entre pantallas
    data object NavigateToLogin : AuthIntent()
    data object NavigateToRegister : AuthIntent()

    // Intents de Login
    data class UpdateLoginEmail(val email: String) : AuthIntent()
    data class UpdateLoginPassword(val password: String) : AuthIntent()
    data object ToggleLoginPasswordVisibility : AuthIntent()
    data object SubmitLogin : AuthIntent()

    // Intents de Registro
    data class UpdateRegisterUsername(val username: String) : AuthIntent()
    data class UpdateRegisterEmail(val email: String) : AuthIntent()
    data class UpdateRegisterPassword(val password: String) : AuthIntent()
    data object ToggleRegisterPasswordVisibility : AuthIntent()
    data class UpdateProfileImage(val uri: Uri?) : AuthIntent()
    data object SubmitRegister : AuthIntent()

    // Intent para limpiar errores
    data object ClearError : AuthIntent()
}
