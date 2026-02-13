package es.rafapuig.pmdm.compose.proyecto.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LibroListaDto(
    @SerializedName("id")
    val id: Long? = null,

    @SerializedName("idLista")
    val idLista: Long,

    @SerializedName("googleBookId")
    val googleBookId: String,

    @SerializedName("resena")
    val resena: String? = null,

    @SerializedName("puntuacion")
    val puntuacion: Int? = null,

    @SerializedName("fechaPublicacion")
    val fechaPublicacion: String? = null
)
