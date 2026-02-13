package es.rafapuig.pmdm.compose.proyecto.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ListaDto(
    @SerializedName("idLista")
    val idLista: Long? = null,

    @SerializedName("idUsuario")
    val idUsuario: Long,

    @SerializedName("nombreLista")
    val nombreLista: String
)
