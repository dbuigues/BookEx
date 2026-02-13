package es.rafapuig.pmdm.compose.proyecto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import es.rafapuig.pmdm.compose.proyecto.navigation.NavigationRoot
import es.rafapuig.pmdm.compose.proyecto.ui.theme.ProyectoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProyectoTheme {
                NavigationRoot()
            }
        }
    }
}
