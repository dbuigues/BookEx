package es.rafapuig.pmdm.compose.proyecto.feature.auth.view

import androidx.compose.animation.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import es.rafapuig.pmdm.compose.proyecto.feature.auth.model.AuthScreen
import es.rafapuig.pmdm.compose.proyecto.feature.auth.viewmodel.AuthViewModel

/**
 * Pantalla contenedora de autenticación que gestiona la navegación
 * entre Login y Registro siguiendo el patrón MVI
 */
@Composable
fun AuthScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    AnimatedContent(
        targetState = state.currentScreen,
        modifier = modifier.fillMaxSize(),
        transitionSpec = {
            if (targetState == AuthScreen.REGISTER) {
                // Animación de Login a Registro (deslizar hacia la izquierda)
                slideInHorizontally { width -> width } + fadeIn() togetherWith
                        slideOutHorizontally { width -> -width } + fadeOut()
            } else {
                // Animación de Registro a Login (deslizar hacia la derecha)
                slideInHorizontally { width -> -width } + fadeIn() togetherWith
                        slideOutHorizontally { width -> width } + fadeOut()
            }.using(
                SizeTransform(clip = false)
            )
        },
        label = "AuthScreenTransition"
    ) { screen ->
        when (screen) {
            AuthScreen.LOGIN -> {
                LoginScreen(
                    state = state,
                    onIntent = viewModel::processIntent
                )
            }
            AuthScreen.REGISTER -> {
                RegisterScreen(
                    state = state,
                    onIntent = viewModel::processIntent
                )
            }
        }
    }
}
