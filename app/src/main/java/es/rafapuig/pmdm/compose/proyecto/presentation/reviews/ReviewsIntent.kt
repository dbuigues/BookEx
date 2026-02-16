package es.rafapuig.pmdm.compose.proyecto.presentation.reviews

sealed class ReviewsIntent {
    object LoadReviews : ReviewsIntent()
    data class DeleteReview(val reviewId: Long) : ReviewsIntent()
}

