package es.rafapuig.pmdm.compose.proyecto.feature.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.rafapuig.pmdm.compose.proyecto.domain.usecase.LoginUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(private val loginUseCase: LoginUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginState())
    val uiState = _uiState.asStateFlow()

    private val _events = Channel<LoginEvent>()
    val events = _events.receiveAsFlow()

    private fun notifyEvent(event: LoginEvent) {
        viewModelScope.launch {
            _events.send(event)
        }
    }

    fun onAction(action: LoginIntent) {
        when (action) {
            is LoginIntent.OnLogin -> {
                onLogin(action.email, action.password)
            }
            LoginIntent.OnNavigateToRegister -> {
                notifyEvent(LoginEvent.NavigateToRegister)
            }
        }
    }

    private fun onLogin(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            loginUseCase.execute(email, password)
                .onSuccess { user ->
                    _uiState.update { it.copy(isLoading = false) }
                    notifyEvent(LoginEvent.LoginSuccess(user.username))
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Error al iniciar sesión"
                        )
                    }
                    notifyEvent(LoginEvent.ShowErrorMessage(error.message ?: "Error al iniciar sesión"))
                }
        }
    }

}