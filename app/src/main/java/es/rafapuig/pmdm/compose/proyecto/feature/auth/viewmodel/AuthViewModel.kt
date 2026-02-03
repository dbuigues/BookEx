package es.rafapuig.pmdm.compose.proyecto.feature.auth.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import es.rafapuig.pmdm.compose.proyecto.feature.auth.intent.AuthIntent
import es.rafapuig.pmdm.compose.proyecto.feature.auth.model.AuthScreen
import es.rafapuig.pmdm.compose.proyecto.feature.auth.model.AuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel para la autenticación siguiendo el patrón MVI
 * Procesa los Intents y actualiza el State
 */
class AuthViewModel : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    /**
     * Procesa los intents del usuario y actualiza el estado
     */
    fun processIntent(intent: AuthIntent) {
        when (intent) {
            // Navegación
            is AuthIntent.NavigateToLogin -> navigateToLogin()
            is AuthIntent.NavigateToRegister -> navigateToRegister()

            // Login
            is AuthIntent.UpdateLoginEmail -> updateLoginEmail(intent.email)
            is AuthIntent.UpdateLoginPassword -> updateLoginPassword(intent.password)
            is AuthIntent.ToggleLoginPasswordVisibility -> toggleLoginPasswordVisibility()
            is AuthIntent.SubmitLogin -> submitLogin()

            // Registro
            is AuthIntent.UpdateRegisterUsername -> updateRegisterUsername(intent.username)
            is AuthIntent.UpdateRegisterEmail -> updateRegisterEmail(intent.email)
            is AuthIntent.UpdateRegisterPassword -> updateRegisterPassword(intent.password)
            is AuthIntent.ToggleRegisterPasswordVisibility -> toggleRegisterPasswordVisibility()
            is AuthIntent.UpdateProfileImage -> updateProfileImage(intent.uri)
            is AuthIntent.SubmitRegister -> submitRegister()

            // Errores
            is AuthIntent.ClearError -> clearError()
        }
    }

    // Funciones de navegación
    private fun navigateToLogin() {
        _state.update { it.copy(currentScreen = AuthScreen.LOGIN, errorMessage = null) }
    }

    private fun navigateToRegister() {
        _state.update { it.copy(currentScreen = AuthScreen.REGISTER, errorMessage = null) }
    }

    // Funciones de Login
    private fun updateLoginEmail(email: String) {
        _state.update { it.copy(loginEmail = email) }
    }

    private fun updateLoginPassword(password: String) {
        _state.update { it.copy(loginPassword = password) }
    }

    private fun toggleLoginPasswordVisibility() {
        _state.update { it.copy(isLoginPasswordVisible = !it.isLoginPasswordVisible) }
    }

    private fun submitLogin() {// TODO: Implementar login

        // Por ahora solo mostramos un mensaje, la lógica real se implementará después
        _state.update { it.copy(isLoading = true) }
        _state.update { it.copy(isLoading = false) }
    }

    // Funciones de Registro
    private fun updateRegisterUsername(username: String) {
        _state.update { it.copy(registerUsername = username) }
    }

    private fun updateRegisterEmail(email: String) {
        _state.update { it.copy(registerEmail = email) }
    }

    private fun updateRegisterPassword(password: String) {
        _state.update { it.copy(registerPassword = password) }
    }

    private fun toggleRegisterPasswordVisibility() {
        _state.update { it.copy(isRegisterPasswordVisible = !it.isRegisterPasswordVisible) }
    }

    private fun updateProfileImage(uri: Uri?) {
        _state.update { it.copy(profileImageUri = uri) }
    }

    private fun submitRegister() { // TODO: Implementar registro
        // Por ahora solo mostramos un mensaje, la lógica real se implementará después
        _state.update { it.copy(isLoading = true) }
        _state.update { it.copy(isLoading = false) }
    }

    // Función para limpiar errores
    private fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }
}
