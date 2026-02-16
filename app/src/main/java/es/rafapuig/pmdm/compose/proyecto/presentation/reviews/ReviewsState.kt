package es.rafapuig.pmdm.compose.proyecto.presentation.reviews

import es.rafapuig.pmdm.compose.proyecto.domain.model.Review

data class ReviewsState(
    val isLoading: Boolean = false,
    val reviews: List<Review> = emptyList(),
    val errorMessage: String? = null
)

