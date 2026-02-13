package es.rafapuig.pmdm.compose.proyecto.feature.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.rafapuig.pmdm.compose.proyecto.domain.usecase.LoginUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
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
                // Handle login action
                onLogin(action.email, action.password)
            }
            LoginIntent.OnNavigateToRegister -> {
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
                    val username = email.substringBefore('@')
                    notifyEvent(LoginEvent.LoginSuccess(username))
                } else {
                    notifyEvent(LoginEvent.ShowErrorMessage("Invalid credentials"))
                }
            }.onFailure { error ->
                notifyEvent(LoginEvent.ShowErrorMessage(error.message ?: "An unexpected error occurred"))
                return@launch
            }
        }
    }

    private fun onNavigateToRegister() {
        // Handle navigation to register screen
        notifyEvent(LoginEvent.NavigateToRegister)
    }

}