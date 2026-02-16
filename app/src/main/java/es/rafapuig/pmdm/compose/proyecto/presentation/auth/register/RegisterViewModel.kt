package es.rafapuig.pmdm.compose.proyecto.presentation.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.rafapuig.pmdm.compose.proyecto.domain.usecase.auth.RegisterUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
                notifyEvent(RegisterEvent.NavigateToLogin)
            }
            is RegisterIntent.OnImageSelected -> {
                _uiState.update { it.copy(profileImageUri = action.uri) }
            }
            RegisterIntent.OnUnselectImage -> {
                _uiState.update { it.copy(profileImageUri = null) }
            }
            RegisterIntent.OnSelectImage -> {
                // Este intent se maneja en la UI para abrir el picker
            }
        }
    }

    private fun onRegister(username: String, email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            if (password != confirmPassword) {
                _uiState.update { it.copy(errorMessage = "Las contraseñas no coinciden") }
                notifyEvent(RegisterEvent.ShowErrorMessage("Las contraseñas no coinciden"))
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            registerUseCase.execute(username, email, password)
                .onSuccess { user ->
                    _uiState.update { it.copy(isLoading = false) }
                    notifyEvent(RegisterEvent.RegisterSuccess)
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Error al registrarse"
                        )
                    }
                    notifyEvent(RegisterEvent.ShowErrorMessage(error.message ?: "Error al registrarse"))
                }
        }
    }

}