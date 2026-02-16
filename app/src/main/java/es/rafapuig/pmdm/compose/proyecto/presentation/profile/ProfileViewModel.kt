package es.rafapuig.pmdm.compose.proyecto.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.rafapuig.pmdm.compose.proyecto.domain.repository.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileState())
    val uiState = _uiState.asStateFlow()

    private val _events = Channel<ProfileEvent>()
    val events = _events.receiveAsFlow()

    init {
        loadProfile()
    }

    private fun notifyEvent(event: ProfileEvent) {
        viewModelScope.launch {
            _events.send(event)
        }
    }

    fun onAction(action: ProfileIntent) {
        when (action) {
            ProfileIntent.LoadProfile -> loadProfile()
            ProfileIntent.Logout -> logout()
            ProfileIntent.RefreshProfile -> loadProfile()
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val user = authRepository.getCurrentUser()
                _uiState.update {
                    it.copy(
                        user = user,
                        isLoading = false,
                        errorMessage = if (user == null) "No se pudo cargar el perfil" else null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error al cargar el perfil"
                    )
                }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                authRepository.logout()
                _uiState.update { it.copy(isLoading = false, user = null) }
                notifyEvent(ProfileEvent.LogoutSuccess)
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                notifyEvent(ProfileEvent.ShowErrorMessage(e.message ?: "Error al cerrar sesión"))
            }
        }
    }
}
