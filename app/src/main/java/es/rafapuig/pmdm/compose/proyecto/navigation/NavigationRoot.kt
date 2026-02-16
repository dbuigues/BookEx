package es.rafapuig.pmdm.compose.proyecto.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import es.rafapuig.pmdm.compose.proyecto.data.local.TokenManager
import es.rafapuig.pmdm.compose.proyecto.presentation.auth.login.LoginRoute
import es.rafapuig.pmdm.compose.proyecto.presentation.auth.register.RegisterRoute

@Composable
fun NavigationRoot() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    // Verificar si hay una sesión activa
    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val isLoggedIn = tokenManager.getUserId() != -1L
        startDestination = if (isLoggedIn) Routes.MAIN_GRAPH else Routes.LOGIN_SCREEN
    }

    // Mostrar contenido solo cuando se determine el destino inicial
    if (startDestination == null) return

    NavHost(navController = navController, startDestination = startDestination!!) {
        composable(Routes.LOGIN_SCREEN) {
            LoginRoute(
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER_SCREEN)
                },
                onLoginSuccess = { _ ->
                    navController.navigate(Routes.MAIN_GRAPH) {
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
                    navController.navigate(Routes.MAIN_GRAPH) {
                        popUpTo(Routes.LOGIN_SCREEN) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.MAIN_GRAPH) {
            MainScreen(
                onLogout = {
                    // Limpiar datos de sesión
                    tokenManager.clearAll()
                    navController.navigate(Routes.LOGIN_SCREEN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}