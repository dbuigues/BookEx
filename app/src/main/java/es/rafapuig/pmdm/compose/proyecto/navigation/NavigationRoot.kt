package es.rafapuig.pmdm.compose.proyecto.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import es.rafapuig.pmdm.compose.proyecto.feature.auth.presentacion.login.LoginRoute
import es.rafapuig.pmdm.compose.proyecto.feature.auth.presentacion.register.RegisterRoute
import es.rafapuig.pmdm.compose.proyecto.feature.home.presentacion.HomeScreen

@Composable
fun NavigationRoot() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.LOGIN_SCREEN) {
        composable(Routes.LOGIN_SCREEN) {
            LoginRoute(
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER_SCREEN)
                },
                onLoginSuccess = { username ->
                    navController.navigate(Routes.homeScreen(username)) {
                        popUpTo(Routes.LOGIN_SCREEN) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.REGISTER_SCREEN) {
            RegisterRoute(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Routes.LOGIN_SCREEN) {
                        popUpTo(Routes.REGISTER_SCREEN) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = Routes.HOME_SCREEN,
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) {
            val username = it.arguments?.getString("username") ?: ""
            HomeScreen(username = username)
        }
    }
}