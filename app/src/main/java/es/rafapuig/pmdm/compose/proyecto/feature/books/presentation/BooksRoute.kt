package es.rafapuig.pmdm.compose.proyecto.feature.books.presentation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun BooksRoute(
    viewModel: BooksViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is BooksEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    BooksScreen(
        state = uiState,
        snackbarHostState = snackbarHostState,
        onQueryChange = viewModel::onQueryChange,
        onSearch = viewModel::onSearch,
        onBookClick = viewModel::onBookClick,
        onDismissBookDetail = viewModel::onDismissBookDetail
    )
}

