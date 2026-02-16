package es.rafapuig.pmdm.compose.proyecto.feature.books.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.rafapuig.pmdm.compose.proyecto.domain.model.Book
import es.rafapuig.pmdm.compose.proyecto.feature.books.domain.SearchBooksUseCase
import kotlinx.coroutines.channels.Channel
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

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query, error = null) }
    }

    fun onSearch() {
        val query = uiState.value.query.trim()

        viewModelScope.launch {
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
    }

    fun onBookClick(book: Book) {
        _uiState.update { it.copy(selectedBook = book) }
    }

    fun onDismissBookDetail() {
        _uiState.update { it.copy(selectedBook = null) }
    }
}

