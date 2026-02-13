package es.rafapuig.pmdm.compose.proyecto.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GoogleBookDto(
    @SerializedName("googleBookId")
    val googleBookId: String,

    @SerializedName("isbn")
    val isbn: String? = null,

    @SerializedName("titulo")
    val titulo: String,

    @SerializedName("autores")
    val autores: List<String>? = null,

    @SerializedName("editorial")
    val editorial: String? = null,

    @SerializedName("fechaPublicacion")
    val fechaPublicacion: String? = null,

    @SerializedName("descripcion")
    val descripcion: String? = null,

    @SerializedName("numeroPaginas")
    val numeroPaginas: Int? = null,

    @SerializedName("categorias")
    val categorias: List<String>? = null,

    @SerializedName("idioma")
    val idioma: String? = null,

    @SerializedName("imagenPortada")
    val imagenPortada: String? = null,

    @SerializedName("imagenPequena")
    val imagenPequena: String? = null,

    @SerializedName("puntuacionMedia")
    val puntuacionMedia: Double? = null,

    @SerializedName("cantidadResenas")
    val cantidadResenas: Int? = null,

    @SerializedName("linkPreview")
    val linkPreview: String? = null
)
