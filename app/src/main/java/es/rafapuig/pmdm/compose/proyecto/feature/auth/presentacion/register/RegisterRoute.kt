package es.rafapuig.pmdm.compose.proyecto.feature.auth.presentacion.register

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegisterRoute(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: RegisterViewModel = koinViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    LaunchedEffect(viewModel.events, context) {
        viewModel.events.collect { event ->
            when (event) {
                is RegisterUiEvent.RegisterSuccess -> {
                    onRegisterSuccess()
                }
                is RegisterUiEvent.NavigateToLogin -> {
                    onNavigateToLogin()
                }
                is RegisterUiEvent.ShowErrorMessage -> {
                    snackbarHostState.showSnackbar(
                        "",//context.getString(event.error.mapToMessage())
                    )
                }
            }
        }
    }

    RegisterScreen(
        state = uiState,
        onAction = viewModel::onAction,
        snackbarHostState = snackbarHostState
    )
}