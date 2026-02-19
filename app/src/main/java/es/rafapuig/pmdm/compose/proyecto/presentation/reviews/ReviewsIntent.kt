package es.rafapuig.pmdm.compose.proyecto.presentation.reviews

sealed class ReviewsIntent {
    object LoadReviews : ReviewsIntent()
    object LoadPublicReviews : ReviewsIntent()
    data class SelectTab(val tab: Int) : ReviewsIntent()
    data class DeleteReview(val reviewId: Long) : ReviewsIntent()
    data class SearchPublicReviews(val query: String) : ReviewsIntent()
}

