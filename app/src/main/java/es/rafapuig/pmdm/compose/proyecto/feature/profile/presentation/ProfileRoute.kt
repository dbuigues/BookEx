package es.rafapuig.pmdm.compose.proyecto.feature.profile.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileRoute(
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.events) {
        viewModel.events.collect { event ->
            when (event) {
                is ProfileEvent.LogoutSuccess -> {
                    onLogout()
                }
                is ProfileEvent.ShowErrorMessage -> {
                    // Manejar error si es necesario
                }
            }
        }
    }

    ProfileScreen(
        state = uiState,
        onAction = viewModel::onAction
    )
}
