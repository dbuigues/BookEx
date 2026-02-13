package es.rafapuig.pmdm.compose.proyecto.data.remote.dto

import com.google.gson.annotations.SerializedName

data class BookListDto(
    @SerializedName("id")
    val id: Long? = null,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("userId")
    val userId: Long,

    @SerializedName("isPublic")
    val isPublic: Boolean = false,

    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("books")
    val books: List<BookDto>? = null
)

data class BookListCreateRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("isPublic")
    val isPublic: Boolean = false
)

data class AddBookToListRequest(
    @SerializedName("bookId")
    val bookId: Long
)
