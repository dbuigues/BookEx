package es.rafapuig.pmdm.compose.proyecto.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import es.rafapuig.pmdm.compose.proyecto.feature.auth.presentacion.login.LoginRoute

@Composable
fun NavigationRoot(startRoute : NavKey = LoginKey) {

    val backStack = rememberNavBackStack(startRoute)

    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider {

            entry<LoginKey> {
                LoginRoute(
                    onLoginSuccess = {
                        backStack.clear()
                        backStack.add(HomeKey)
                    },
                    onNavigateToRegister = {
                        backStack.add(RegisterKey)
                    }
                )
            }

        }


    )


}