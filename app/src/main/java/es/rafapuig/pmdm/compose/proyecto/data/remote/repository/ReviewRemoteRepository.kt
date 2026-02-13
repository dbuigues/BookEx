package es.rafapuig.pmdm.compose.proyecto.data.remote.repository

import es.rafapuig.pmdm.compose.proyecto.data.remote.ApiResponse
import es.rafapuig.pmdm.compose.proyecto.data.remote.api.ReviewApiService
import es.rafapuig.pmdm.compose.proyecto.data.remote.dto.ReviewCreateRequest
import es.rafapuig.pmdm.compose.proyecto.data.remote.dto.ReviewDto
import es.rafapuig.pmdm.compose.proyecto.data.remote.safeApiCall

class ReviewRemoteRepository(private val reviewApiService: ReviewApiService) {

    suspend fun getReviewsByBookId(bookId: Long): ApiResponse<List<ReviewDto>> {
        return safeApiCall { reviewApiService.getReviewsByBookId(bookId) }
    }

    suspend fun getReviewsByUserId(userId: Long): ApiResponse<List<ReviewDto>> {
        return safeApiCall { reviewApiService.getReviewsByUserId(userId) }
    }

    suspend fun createReview(token: String, bookId: Long, rating: Int, comment: String?): ApiResponse<ReviewDto> {
        val request = ReviewCreateRequest(bookId, rating, comment)
        return safeApiCall { reviewApiService.createReview("Bearer $token", request) }
    }

    suspend fun updateReview(token: String, id: Long, bookId: Long, rating: Int, comment: String?): ApiResponse<ReviewDto> {
        val request = ReviewCreateRequest(bookId, rating, comment)
        return safeApiCall { reviewApiService.updateReview("Bearer $token", id, request) }
    }

    suspend fun deleteReview(token: String, id: Long): ApiResponse<Void> {
        return safeApiCall { reviewApiService.deleteReview("Bearer $token", id) }
    }
}
