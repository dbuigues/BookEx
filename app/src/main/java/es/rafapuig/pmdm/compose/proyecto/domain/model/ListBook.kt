package es.rafapuig.pmdm.compose.proyecto.domain.model

/**
 * Modelo de dominio para un libro dentro de una lista
 */
data class ListBook(
    val id: Long,
    val googleBookId: String,
    val title: String,
    val author: String,
    val coverUrl: String?,
    val review: String? = null,
    val rating: Int? = null
)

