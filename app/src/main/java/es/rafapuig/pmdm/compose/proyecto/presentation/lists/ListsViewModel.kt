package es.rafapuig.pmdm.compose.proyecto.presentation.lists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.rafapuig.pmdm.compose.proyecto.domain.model.BookList
import es.rafapuig.pmdm.compose.proyecto.domain.repository.ListsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ListsViewModel(private val repo: ListsRepository, private val ownerId: Long) : ViewModel() {

    private val _lists = MutableStateFlow<List<BookList>>(emptyList())
    val lists: StateFlow<List<BookList>> get() = _lists

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    init {
        loadLists()
    }

    fun loadLists() {
        viewModelScope.launch {
            try {
                val data = repo.getListsForOwner(ownerId)
                _lists.value = data
            } catch (e: Exception) {
                _error.value = e.message
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
