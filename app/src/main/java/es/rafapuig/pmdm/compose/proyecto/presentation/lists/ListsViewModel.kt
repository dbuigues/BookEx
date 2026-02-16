package es.rafapuig.pmdm.compose.proyecto.presentation.lists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.rafapuig.pmdm.compose.proyecto.domain.model.BookList
import es.rafapuig.pmdm.compose.proyecto.domain.model.ListBook
import es.rafapuig.pmdm.compose.proyecto.domain.repository.ListsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ListsViewModel(private val repo: ListsRepository, private val ownerId: Long) : ViewModel() {

    private val _lists = MutableStateFlow<List<BookList>>(emptyList())
    val lists: StateFlow<List<BookList>> get() = _lists

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    // Estado para la lista seleccionada y sus libros
    private val _selectedList = MutableStateFlow<BookList?>(null)
    val selectedList: StateFlow<BookList?> get() = _selectedList

    private val _booksInList = MutableStateFlow<List<ListBook>>(emptyList())
    val booksInList: StateFlow<List<ListBook>> get() = _booksInList

    private val _isLoadingBooks = MutableStateFlow(false)
    val isLoadingBooks: StateFlow<Boolean> get() = _isLoadingBooks

    init {
        loadLists()
    }

    fun loadLists() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val data = repo.getListsForOwner(ownerId)
                _lists.value = data
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectList(list: BookList) {
        _selectedList.value = list
        loadBooksInList(list.id)
    }

    fun clearSelectedList() {
        _selectedList.value = null
        _booksInList.value = emptyList()
    }

    private fun loadBooksInList(listId: String) {
        viewModelScope.launch {
            _isLoadingBooks.value = true
            _error.value = null
            try {
                val books = repo.getBooksInList(listId)
                _booksInList.value = books
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al cargar los libros"
            } finally {
                _isLoadingBooks.value = false
            }
        }
    }

    fun removeBookFromList(bookListId: Long) {
        viewModelScope.launch {
            try {
                val success = repo.removeBookFromList(bookListId)
                if (success) {
                    // Recargar los libros de la lista actual
                    _selectedList.value?.let { loadBooksInList(it.id) }
                } else {
                    _error.value = "Error al eliminar el libro"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al eliminar el libro"
            }
        }
    }

    fun createList(name: String) {
        if (name.isBlank()) {
            _error.value = "El nombre no puede estar vacío"
            return
        }
        viewModelScope.launch {
            try {
                repo.createList(ownerId, name)
                loadLists()
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al crear la lista"
            }
        }
    }

    fun deleteList(listId: String) {
        viewModelScope.launch {
            try {
                repo.deleteList(ownerId, listId)
                loadLists()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun addBookToList(listId: String, bookId: String) {
        viewModelScope.launch {
            try {
                repo.addBookToList(ownerId, listId, bookId)
                loadLists()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}
