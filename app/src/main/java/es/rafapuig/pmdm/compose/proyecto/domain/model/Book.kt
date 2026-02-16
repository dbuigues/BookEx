package es.rafapuig.pmdm.compose.proyecto.domain.model

/**
 * Modelo de dominio para un libro
 * Representa un libro en la capa de dominio de la aplicación
 */
data class Book(
    val id: String,
    val title: String,
    val author: String,
    val description: String,
    val coverUrl: String?,
    val isbn: String?
)
