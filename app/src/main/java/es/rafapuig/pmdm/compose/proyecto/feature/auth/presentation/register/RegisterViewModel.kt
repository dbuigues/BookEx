package es.rafapuig.pmdm.compose.proyecto.feature.auth.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.rafapuig.pmdm.compose.proyecto.domain.usecase.RegisterUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.onSuccess

class RegisterViewModel(private val registerUseCase: RegisterUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterState())
    val uiState = _uiState.asStateFlow()

    private val _events = Channel<RegisterEvent>()
    val events = _events.receiveAsFlow()

    private fun notifyEvent(event: RegisterEvent) {
        viewModelScope.launch {
            _events.send(event)
        }
    }


    fun onAction(action: RegisterIntent) {
        when (action) {
            is RegisterIntent.OnRegister -> {
                onRegister(action.username, action.email, action.password, action.confirmPassword)
            }
            RegisterIntent.OnNavigateToLogin -> {
                onNavigateToLogin()
            }

            else -> {}
        }
    }


    private fun onRegister(username: String, email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {

            if (password != confirmPassword) {
                notifyEvent(RegisterEvent.ShowErrorMessage("Las contraseñas no coinciden"))
                return@launch
            }

            runCatching {
                registerUseCase.execute(username, email, password)
            }.onSuccess { success ->
                if (success) {
                    notifyEvent(RegisterEvent.RegisterSuccess)
                } else {
                    notifyEvent(RegisterEvent.ShowErrorMessage("Registration failed"))
                }
            }.onFailure { error ->
                notifyEvent(RegisterEvent.ShowErrorMessage(error.message ?: "An unexpected error occurred"))
            }
        }
    }

    private fun onNavigateToLogin() {
        notifyEvent(RegisterEvent.NavigateToLogin)
    }

}