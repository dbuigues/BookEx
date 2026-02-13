package es.rafapuig.pmdm.compose.proyecto.feature.auth.presentation.register

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

    LaunchedEffect(key1 = viewModel.events) {
        viewModel.events.collect { event ->
            when (event) {
                is RegisterEvent.RegisterSuccess -> {
                    snackbarHostState.showSnackbar("Usuario registrado con éxito")
                    onRegisterSuccess()
                }
                is RegisterEvent.NavigateToLogin -> {
                    onNavigateToLogin()
                }
                is RegisterEvent.ShowErrorMessage -> {
                    snackbarHostState.showSnackbar(
                        "Error en el registro"
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