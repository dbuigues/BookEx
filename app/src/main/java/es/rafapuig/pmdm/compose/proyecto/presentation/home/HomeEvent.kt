package es.rafapuig.pmdm.compose.proyecto.presentation.home

import es.rafapuig.pmdm.compose.proyecto.domain.model.Book

/**
 * Eventos de la pantalla Home
 * Representa eventos que pueden ocurrir en la UI y que requieren acción
 */
sealed class HomeEvent {
    /**
     * Evento cuando se hace clic en un libro
     */
    data class OnBookClick(val book: Book) : HomeEvent()

    /**
     * Evento para cerrar el diálogo de detalles
     */
    object OnDismissBookDetail : HomeEvent()

    /**
     * Evento para mostrar un mensaje de error
     */
    data class ShowError(val message: String) : HomeEvent()
}
