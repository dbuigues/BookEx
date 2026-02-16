package es.rafapuig.pmdm.compose.proyecto.domain.model

/**
 * Modelo de dominio para una reseña de libro
 */
data class Review(
    val id: Long,
    val googleBookId: String,
    val bookTitle: String,
    val bookAuthor: String,
    val bookCoverUrl: String?,
    val reviewText: String,
    val rating: Int,
    val publishedDate: String?
)

