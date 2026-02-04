package es.rafapuig.pmdm.compose.proyecto.feature.auth.domain

sealed interface AuthError {
    object EmptyFields: AuthError
    object InvalidCredentials: AuthError
    object UserAlreadyExists
    object NetworkError
    object UnknownError
}

class AuthException(val error: AuthError) : RuntimeException()