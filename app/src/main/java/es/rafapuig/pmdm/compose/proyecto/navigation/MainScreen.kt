package es.rafapuig.pmdm.compose.proyecto.navigation

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import es.rafapuig.pmdm.compose.proyecto.presentation.books.BooksRoute
import es.rafapuig.pmdm.compose.proyecto.presentation.home.HomeRoute
import es.rafapuig.pmdm.compose.proyecto.presentation.lists.ListsScreen
import es.rafapuig.pmdm.compose.proyecto.presentation.profile.ProfileRoute
import es.rafapuig.pmdm.compose.proyecto.presentation.reviews.ReviewsScreen
import org.koin.androidx.compose.koinViewModel
import java.util.Base64

@Composable
fun MainScreen(
    onLogout: () -> Unit,
    viewModel: MainScreenViewModel = koinViewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    // Refrescar datos del usuario cuando volvemos de una pantalla
    LaunchedEffect(currentDestination?.route) {
        if (currentDestination?.route == Routes.PROFILE_SCREEN) {
            viewModel.refreshUser()
        }
    }

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
                            // Si es el item de perfil y hay foto de perfil, mostrarla
                            if (item is BottomNavItem.Profile && currentUser?.profileImage != null) {
                                ProfileImageIcon(
                                    profileImageBase64 = currentUser?.profileImage,
                                    selected = selected
                                )
                            } else {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.title
                                )
                            }
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
                HomeRoute()
            }
            composable(Routes.BOOKS_SCREEN) {
                BooksRoute()
            }
            composable(Routes.REVIEWS_SCREEN) {
                ReviewsScreen()
            }
            composable(Routes.LISTS_SCREEN) {
                ListsScreen()
            }
            composable(Routes.PROFILE_SCREEN) {
                ProfileRoute(onLogout = {
                    onLogout()
                })
            }
        }
    }
}

@Composable
private fun ProfileImageIcon(
    profileImageBase64: String?,
    selected: Boolean
) {
    val bitmap = profileImageBase64?.let { base64String ->
        if (base64String.isNotBlank()) {
            try {
                val imageBytes = Base64.getDecoder().decode(base64String)
                BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            } catch (_: Exception) {
                null
            }
        } else {
            null
        }
    }

    if (bitmap != null) {
        Box(
            modifier = Modifier.size(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .then(
                        if (selected) {
                            Modifier.background(
                                MaterialTheme.colorScheme.primaryContainer,
                                CircleShape
                            )
                        } else {
                            Modifier
                        }
                    ),
                contentScale = ContentScale.Crop
            )
        }
    } else {
        // Sin imagen o error, mostrar icono por defecto
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Perfil"
        )
    }
}
