package es.rafapuig.pmdm.compose.proyecto.feature.auth.presentacion.login

import androidx.annotation.StringRes
import es.rafapuig.pmdm.compose.proyecto.R
import es.rafapuig.pmdm.compose.proyecto.feature.auth.domain.AuthError

fun AuthError.mapToMessage(): Int {
    return when (this) {
        AuthError.EmptyFields -> R.string.please_fill_in_all_fields
        AuthError.InvalidCredentials -> R.string.invalid_email_or_password
        AuthError.UserAlreadyExists -> R.string.user_already_exists
        AuthError.NetworkError -> R.string.network_error_please_try_again
        AuthError.UnknownError -> R.string.an_unknown_error_occurred
    }
}