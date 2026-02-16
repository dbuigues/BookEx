package es.rafapuig.pmdm.compose.proyecto.domain.model

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class BookList(
    val id: String,
    val name: String,
    val ownerId: Long,
    val createdAt: String,
    val items: MutableList<String> = mutableListOf()
) : Parcelable

