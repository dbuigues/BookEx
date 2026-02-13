package es.rafapuig.pmdm.compose.proyecto.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ReviewDto(
    @SerializedName("id")
    val id: Long? = null,

    @SerializedName("bookId")
    val bookId: Long,

    @SerializedName("userId")
    val userId: Long,

    @SerializedName("rating")
    val rating: Int,

    @SerializedName("comment")
    val comment: String? = null,

    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("username")
    val username: String? = null
)

data class ReviewCreateRequest(
    @SerializedName("bookId")
    val bookId: Long,

    @SerializedName("rating")
    val rating: Int,

    @SerializedName("comment")
    val comment: String? = null
)
