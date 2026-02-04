package es.rafapuig.pmdm.compose.proyecto.feature.auth.presentacion.login

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import es.rafapuig.pmdm.compose.proyecto.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginRoute(
    onNavigateToRegister : () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = koinViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current



    LaunchedEffect(viewModel.events, context) {
        viewModel.events.collect { event ->
            when (event) {
                is LoginUiEvent.LoginSuccess -> {
                    // Handle login success, e.g., navigate to the main screen
                    onLoginSuccess()
                }
                is LoginUiEvent.NavigateToRegister -> {
                    // Handle navigation to register screen
                    onNavigateToRegister()
                }
                is LoginUiEvent.ShowErrorMessage -> {
                    // Show error message to the user
                    snackbarHostState.showSnackbar(
                        "",//context.getString(event.error.mapToMessage())
                        )

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