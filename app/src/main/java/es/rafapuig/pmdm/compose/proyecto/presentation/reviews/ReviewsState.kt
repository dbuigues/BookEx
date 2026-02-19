package es.rafapuig.pmdm.compose.proyecto.presentation.reviews

import es.rafapuig.pmdm.compose.proyecto.domain.model.Review

data class ReviewsState(
    val isLoading: Boolean = false,
    val reviews: List<Review> = emptyList(),
    val publicReviews: List<Review> = emptyList(),
    val isLoadingPublic: Boolean = false,
    val errorMessage: String? = null,
    val publicErrorMessage: String? = null,
    val selectedTab: Int = 0,  // 0 = Mis reseñas, 1 = Reseñas públicas
    val publicSearchQuery: String = ""
) {
    val filteredPublicReviews: List<Review>
        get() = if (publicSearchQuery.isBlank()) publicReviews
                else publicReviews.filter {
                    it.bookTitle.contains(publicSearchQuery, ignoreCase = true)
                }
}

