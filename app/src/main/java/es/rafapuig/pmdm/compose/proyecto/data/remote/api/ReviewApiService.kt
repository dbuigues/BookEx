package es.rafapuig.pmdm.compose.proyecto.data.remote.api

import es.rafapuig.pmdm.compose.proyecto.data.remote.dto.ReviewCreateRequest
import es.rafapuig.pmdm.compose.proyecto.data.remote.dto.ReviewDto
import retrofit2.Response
import retrofit2.http.*

interface ReviewApiService {

    @GET("api/reviews/book/{bookId}")
    suspend fun getReviewsByBookId(@Path("bookId") bookId: Long): Response<List<ReviewDto>>

    @GET("api/reviews/user/{userId}")
    suspend fun getReviewsByUserId(@Path("userId") userId: Long): Response<List<ReviewDto>>

    @POST("api/reviews")
    suspend fun createReview(
        @Header("Authorization") token: String,
        @Body review: ReviewCreateRequest
    ): Response<ReviewDto>

    @PUT("api/reviews/{id}")
    suspend fun updateReview(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Body review: ReviewCreateRequest
    ): Response<ReviewDto>

    @DELETE("api/reviews/{id}")
    suspend fun deleteReview(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<Void>
}
