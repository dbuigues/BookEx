package es.rafapuig.pmdm.compose.proyecto.presentation.books

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.rafapuig.pmdm.compose.proyecto.domain.model.Book
import es.rafapuig.pmdm.compose.proyecto.domain.usecase.books.SearchBooksUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BooksViewModel(
    private val searchBooksUseCase: SearchBooksUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BooksState())
    val uiState = _uiState.asStateFlow()

    private val _events = Channel<BooksEvent>()
    val events = _events.receiveAsFlow()

    private var searchJob: Job? = null

    companion object {
        private const val DEBOUNCE_DELAY_MS = 1000L
    }

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query, error = null) }

        // Cancelar búsqueda anterior si existe
        searchJob?.cancel()

        // Iniciar nueva búsqueda con debounce
        if (query.trim().isNotBlank()) {
            searchJob = viewModelScope.launch {
                delay(DEBOUNCE_DELAY_MS)
                performSearch(query.trim())
            }
        } else {
            // Si el query está vacío, limpiar resultados
            _uiState.update { it.copy(results = emptyList()) }
        }
    }

    fun onSearch() {
        val query = uiState.value.query.trim()
        if (query.isBlank()) return

        // Cancelar debounce y buscar inmediatamente
        searchJob?.cancel()
        viewModelScope.launch {
            performSearch(query)
        }
    }

    private suspend fun performSearch(query: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }

        searchBooksUseCase(query)
            .onSuccess { books ->
                _uiState.update { it.copy(isLoading = false, results = books, error = null) }
            }
            .onFailure { t ->
                val msg = t.message ?: "Error al buscar libros"
                _uiState.update { it.copy(isLoading = false, error = msg) }
                _events.send(BooksEvent.ShowError(msg))
            }
    }

    fun onBookClick(book: Book) {
        _uiState.update { it.copy(selectedBook = book) }
    }

    fun onDismissBookDetail() {
        _uiState.update { it.copy(selectedBook = null) }
    }
}

