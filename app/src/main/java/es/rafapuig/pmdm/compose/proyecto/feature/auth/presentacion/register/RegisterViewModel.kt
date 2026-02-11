package es.rafapuig.pmdm.compose.proyecto.feature.auth.presentacion.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.rafapuig.pmdm.compose.proyecto.feature.auth.domain.usecase.RegisterUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.onSuccess

class RegisterViewModel(private val registerUseCase: RegisterUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = Channel<RegisterUiEvent>()
    val events = _events.receiveAsFlow()

    private fun notifyEvent(event: RegisterUiEvent) {
        viewModelScope.launch {
            _events.send(event)
        }
    }


    fun onAction(action: RegisterAction) {
        when (action) {
            is RegisterAction.OnRegister -> {
                onRegister(action.username, action.email, action.password, action.confirmPassword)
            }
            RegisterAction.OnNavigateToLogin -> {
                onNavigateToLogin()
            }

            else -> {}
        }
    }


    private fun onRegister(username: String, email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {

            if (password != confirmPassword) {
                notifyEvent(RegisterUiEvent.ShowErrorMessage("Las contraseñas no coinciden"))
                return@launch
            }

            runCatching {
                registerUseCase.execute(username, email, password)
            }.onSuccess { success ->
                if (success) {
                    notifyEvent(RegisterUiEvent.RegisterSuccess)
                } else {
                    notifyEvent(RegisterUiEvent.ShowErrorMessage("Registration failed"))
                }
            }.onFailure { error ->
                notifyEvent(RegisterUiEvent.ShowErrorMessage(error.message ?: "An unexpected error occurred"))
            }
        }
    }

    private fun onNavigateToLogin() {
        notifyEvent(RegisterUiEvent.NavigateToLogin)
    }

}