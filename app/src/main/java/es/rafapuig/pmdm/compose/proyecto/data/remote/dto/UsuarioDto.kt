package es.rafapuig.pmdm.compose.proyecto.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UsuarioDto(
    @SerializedName("idUsuario")
    val idUsuario: Long? = null,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("correo")
    val correo: String,

    @SerializedName("contrasena")
    val contrasena: String? = null,

    @SerializedName("fotoPerfil")
    val fotoPerfil: String? = null
)

data class LoginRequest(
    @SerializedName("correo")
    val correo: String,

    @SerializedName("contrasena")
    val contrasena: String
)
