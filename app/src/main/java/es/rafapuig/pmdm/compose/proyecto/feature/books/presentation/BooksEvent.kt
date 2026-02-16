package es.rafapuig.pmdm.compose.proyecto.feature.books.presentation

sealed class BooksEvent {
    data class ShowError(val message: String) : BooksEvent()
}

