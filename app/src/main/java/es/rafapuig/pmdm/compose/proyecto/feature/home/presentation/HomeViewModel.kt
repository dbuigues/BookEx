package es.rafapuig.pmdm.compose.proyecto.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.rafapuig.pmdm.compose.proyecto.data.local.TokenManager
import es.rafapuig.pmdm.compose.proyecto.domain.model.Book
import es.rafapuig.pmdm.compose.proyecto.feature.home.domain.GetPopularBooksUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla Home
 * Maneja la lógica de negocio y el estado de la UI
 */
class HomeViewModel(
    private val getPopularBooksUseCase: GetPopularBooksUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeState())
    val uiState = _uiState.asStateFlow()

    private val _events = Channel<HomeEvent>()
    val events = _events.receiveAsFlow()

    init {
        loadUserData()
        loadPopularBooks()
    }

    private fun loadUserData() {
        val username = tokenManager.getUsername()
        _uiState.update { it.copy(username = username) }
    }

    fun loadPopularBooks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            getPopularBooksUseCase()
                .onSuccess { books ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            books = books,
                            error = null
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Error al cargar los libros"
                        )
                    }
                    notifyEvent(HomeEvent.ShowError(error.message ?: "Error al cargar los libros"))
                }
        }
    }

    /**
     * Maneja el clic en un libro
     */
    fun onBookClick(book: Book) {
        _uiState.update { it.copy(selectedBook = book) }
        notifyEvent(HomeEvent.OnBookClick(book))
    }

    /**
     * Cierra el diálogo de detalles del libro
     */
    fun onDismissBookDetail() {
        _uiState.update { it.copy(selectedBook = null) }
        notifyEvent(HomeEvent.OnDismissBookDetail)
    }

    /**
     * Notifica un evento al composable
     */
    private fun notifyEvent(event: HomeEvent) {
        viewModelScope.launch {
            _events.send(event)
        }
    }

    /**
     * Recarga los libros populares
     */
    fun refreshBooks() {
        loadPopularBooks()
    }
}
