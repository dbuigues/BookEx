package es.rafapuig.pmdm.compose.proyecto.feature.home.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

/**
 * Route composable para la pantalla Home
 * Conecta el ViewModel con la UI y maneja los eventos
 */
@Composable
fun HomeRoute(
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.events) {
        viewModel.events.collect { event ->
            when (event) {
                is HomeEvent.OnBookClick -> {
                    // El diálogo se muestra automáticamente a través del estado
                }
                is HomeEvent.OnDismissBookDetail -> {
                    // El diálogo se cierra automáticamente a través del estado
                }
                is HomeEvent.ShowError -> {
                    // El error ya está en el estado, se muestra en la UI
                }
            }
        }
    }

    HomeScreen(
        state = uiState,
        onBookClick = { book ->
            viewModel.onBookClick(book)
        },
        onDismissBookDetail = {
            viewModel.onDismissBookDetail()
        }
    )
}
