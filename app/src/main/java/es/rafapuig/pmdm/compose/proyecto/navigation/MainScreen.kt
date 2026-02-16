package es.rafapuig.pmdm.compose.proyecto.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import es.rafapuig.pmdm.compose.proyecto.feature.books.presentation.BooksScreen
import es.rafapuig.pmdm.compose.proyecto.feature.home.presentation.HomeScreen
import es.rafapuig.pmdm.compose.proyecto.feature.lists.presentation.ListsScreen
import es.rafapuig.pmdm.compose.proyecto.feature.profile.presentation.ProfileRoute
import es.rafapuig.pmdm.compose.proyecto.feature.reviews.presentation.ReviewsScreen

@Composable
fun MainScreen(
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                BottomNavItem.items.forEach { item ->
                    val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true

                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title
                            )
                        },
                        label = { Text(item.title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME_SCREEN,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.HOME_SCREEN) {
                HomeScreen()
            }
            composable(Routes.BOOKS_SCREEN) {
                BooksScreen()
            }
            composable(Routes.REVIEWS_SCREEN) {
                ReviewsScreen()
            }
            composable(Routes.LISTS_SCREEN) {
                ListsScreen()
            }
            composable(Routes.PROFILE_SCREEN) {
                ProfileRoute(onLogout = onLogout)
            }
        }
    }
}
