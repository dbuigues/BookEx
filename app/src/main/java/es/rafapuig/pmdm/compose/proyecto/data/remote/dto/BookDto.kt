package es.rafapuig.pmdm.compose.proyecto.data.remote.dto

import com.google.gson.annotations.SerializedName

data class BookDto(
    @SerializedName("id")
    val id: Long? = null,

    @SerializedName("title")
    val title: String,

    @SerializedName("author")
    val author: String,

    @SerializedName("isbn")
    val isbn: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("coverImage")
    val coverImage: String? = null,

    @SerializedName("publicationYear")
    val publicationYear: Int? = null,

    @SerializedName("genre")
    val genre: String? = null,

    @SerializedName("averageRating")
    val averageRating: Double? = null
)

data class BookCreateRequest(
    @SerializedName("title")
    val title: String,

    @SerializedName("author")
    val author: String,

    @SerializedName("isbn")
    val isbn: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("coverImage")
    val coverImage: String? = null,

    @SerializedName("publicationYear")
    val publicationYear: Int? = null,

    @SerializedName("genre")
    val genre: String? = null
)
