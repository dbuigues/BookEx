package es.rafapuig.pmdm.compose.proyecto.domain.model

data class User(
    val id: Long,
    val username: String,
    val email: String,
    val profileImage: String? = null
)
