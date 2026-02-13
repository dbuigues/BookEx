package es.rafapuig.pmdm.compose.proyecto.domain

sealed interface AuthError {
    object EmptyFields : AuthError
    object InvalidEmail : AuthError
    object WeakPassword : AuthError
    object InvalidCredentials : AuthError
    object UserAlreadyExists : AuthError
    object NetworkError : AuthError
    object UnknownError : AuthError
}

class AuthException(val error: AuthError) : RuntimeException() {
    override val message: String
        get() = when (error) {
            AuthError.EmptyFields -> "Por favor, completa todos los campos"
            AuthError.InvalidEmail -> "El correo electrónico no es válido"
            AuthError.WeakPassword -> "La contraseña debe tener al menos 6 caracteres"
            AuthError.InvalidCredentials -> "Credenciales inválidas"
            AuthError.UserAlreadyExists -> "El usuario ya existe"
            AuthError.NetworkError -> "Error de conexión"
            AuthError.UnknownError -> "Ha ocurrido un error inesperado"
        }
}
