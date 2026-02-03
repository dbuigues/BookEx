package es.rafapuig.pmdm.compose.proyecto.feature.auth.model

import android.net.Uri

/**
 * Estado para la pantalla de autenticación (Login/Registro)
 * Siguiendo el patrón MVI, este es el Model que representa el estado de la UI
 */
data class AuthState(
    // Estado común
    val currentScreen: AuthScreen = AuthScreen.LOGIN,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,

    // Estado de Login
    val loginEmail: String = "",
    val loginPassword: String = "",
    val isLoginPasswordVisible: Boolean = false,

    // Estado de Registro
    val registerUsername: String = "",
    val registerEmail: String = "",
    val registerPassword: String = "",
    val isRegisterPasswordVisible: Boolean = false,
    val profileImageUri: Uri? = null
)

/**
 * Enum que representa las pantallas de autenticación disponibles
 */
enum class AuthScreen {
    LOGIN,
    REGISTER
}
