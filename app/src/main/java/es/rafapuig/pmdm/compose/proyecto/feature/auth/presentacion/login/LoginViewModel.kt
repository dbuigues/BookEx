package es.rafapuig.pmdm.compose.proyecto.feature.auth.presentacion.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.rafapuig.pmdm.compose.proyecto.feature.auth.domain.usecase.LoginUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val loginUseCase: LoginUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = Channel<LoginUiEvent>()
    val events = _events.receiveAsFlow()

    private fun notifyEvent(event: LoginUiEvent) {
        viewModelScope.launch {
            _events.send(event)
        }
    }


    fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.OnLogin -> {
                // Handle login action
                onLogin(action.email, action.password)
            }
            LoginAction.OnNavigateToRegister -> {
                // Handle navigation to register action
                onNavigateToRegister()
            }
        }
    }


    private fun onLogin(email: String, password:String) {
        viewModelScope.launch {

            runCatching {
                loginUseCase.execute(email, password)
            }.onSuccess { success ->
                if (success) {
                    notifyEvent(LoginUiEvent.LoginSuccess)
                } else {
                    notifyEvent(LoginUiEvent.ShowErrorMessage("Invalid credentials"))
                }
            }.onFailure { error ->
                notifyEvent(LoginUiEvent.ShowErrorMessage(error.message ?: "An unexpected error occurred"))
                return@launch
            }
        }
    }

    private fun onNavigateToRegister() {
        // Handle navigation to register screen
        notifyEvent(LoginUiEvent.NavigateToRegister)
    }

}