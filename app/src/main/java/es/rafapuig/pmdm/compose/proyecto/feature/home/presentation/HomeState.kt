package es.rafapuig.pmdm.compose.proyecto.feature.home.presentation

import es.rafapuig.pmdm.compose.proyecto.domain.model.Book

/**
 * Estado de la pantalla Home
 * Contiene toda la información necesaria para renderizar la UI
 */
data class HomeState(
    val isLoading: Boolean = false,
    val books: List<Book> = emptyList(),
    val error: String? = null,
    val username: String? = null,
    val selectedBook: Book? = null
) {
    /**
     * Indica si hay un error activo
     */
    val hasError: Boolean
        get() = error != null

    /**
     * Indica si hay libros cargados
     */
    val hasBooks: Boolean
        get() = books.isNotEmpty()

    /**
     * Indica si hay un libro seleccionado (diálogo abierto)
     */
    val isBookDetailOpen: Boolean
        get() = selectedBook != null
}
