package es.rafapuig.pmdm.compose.proyecto.presentation.books

import es.rafapuig.pmdm.compose.proyecto.domain.model.Book

data class BooksState(
    val query: String = "",
    val isLoading: Boolean = false,
    val results: List<Book> = emptyList(),
    val error: String? = null,
    val selectedBook: Book? = null
) {
    val hasError: Boolean get() = error != null
    val hasResults: Boolean get() = results.isNotEmpty()
}

