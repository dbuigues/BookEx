package es.rafapuig.pmdm.compose.proyecto.feature.auth.presentation.register

import android.net.Uri

data class RegisterState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val profileImageUri: Uri? = null
)
