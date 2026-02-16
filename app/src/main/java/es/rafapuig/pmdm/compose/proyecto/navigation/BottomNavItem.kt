package es.rafapuig.pmdm.compose.proyecto.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.RateReview
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : BottomNavItem(
        route = Routes.HOME_SCREEN,
        title = "Inicio",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )

    object Books : BottomNavItem(
        route = Routes.BOOKS_SCREEN,
        title = "Libros",
        selectedIcon = Icons.Filled.Book,
        unselectedIcon = Icons.Outlined.Book
    )

    object Reviews : BottomNavItem(
        route = Routes.REVIEWS_SCREEN,
        title = "Reseñas",
        selectedIcon = Icons.Filled.RateReview,
        unselectedIcon = Icons.Outlined.RateReview
    )

    object Lists : BottomNavItem(
        route = Routes.LISTS_SCREEN,
        title = "Listas",
        selectedIcon = Icons.AutoMirrored.Filled.List,
        unselectedIcon = Icons.AutoMirrored.Outlined.List
    )

    object Profile : BottomNavItem(
        route = Routes.PROFILE_SCREEN,
        title = "Perfil",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )

    companion object {
        val items = listOf(Home, Books, Reviews, Lists, Profile)
    }
}
