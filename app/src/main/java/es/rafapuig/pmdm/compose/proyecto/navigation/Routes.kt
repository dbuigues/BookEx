package es.rafapuig.pmdm.compose.proyecto.navigation

object Routes {
    const val LOGIN_SCREEN = "login_screen"
    const val REGISTER_SCREEN = "register_screen"
    const val HOME_SCREEN = "home_screen/{username}"
    const val PROFILE_SCREEN = "profile_screen"

    fun homeScreen(username: String) = "home_screen/$username"
}