package es.rafapuig.pmdm.compose.proyecto.feature.auth.presentation.login

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginRoute(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: (String) -> Unit,
    viewModel: LoginViewModel = koinViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.events) {
        viewModel.events.collect { event ->
            when (event) {
                is LoginEvent.LoginSuccess -> {
                    onLoginSuccess(event.username)
                }
                is LoginEvent.NavigateToRegister -> {
                    onNavigateToRegister()
                }
                is LoginEvent.ShowErrorMessage -> {
                    snackbarHostState.showSnackbar(event.error)
                }
            }
        }
    }

    LoginScreen(
        state = uiState,
        onAction = viewModel::onAction,
        snackbarHostState = snackbarHostState
    )
}