package es.rafapuig.pmdm.compose.proyecto.presentation.books

sealed class BooksEvent {
    data class ShowError(val message: String) : BooksEvent()
}

