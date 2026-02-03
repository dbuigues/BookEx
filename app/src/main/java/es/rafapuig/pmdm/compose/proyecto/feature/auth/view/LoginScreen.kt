package es.rafapuig.pmdm.compose.proyecto.feature.auth.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import es.rafapuig.pmdm.compose.proyecto.feature.auth.intent.AuthIntent
import es.rafapuig.pmdm.compose.proyecto.feature.auth.model.AuthState
import es.rafapuig.pmdm.compose.proyecto.ui.theme.ProyectoTheme

/**
 * Pantalla de Login siguiendo el patrón MVI
 * Recibe el estado y emite intents al ViewModel
 */
@Composable
fun LoginScreen(
    state: AuthState,
    onIntent: (AuthIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Título
        Text(
            text = "Iniciar Sesión",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Bienvenido de nuevo a BookEx",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Campo de correo electrónico
        OutlinedTextField(
            value = state.loginEmail,
            onValueChange = { onIntent(AuthIntent.UpdateLoginEmail(it)) },
            label = { Text("Correo electrónico") },
            placeholder = { Text("ejemplo@correo.com") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Email"
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de contraseña
        OutlinedTextField(
            value = state.loginPassword,
            onValueChange = { onIntent(AuthIntent.UpdateLoginPassword(it)) },
            label = { Text("Contraseña") },
            placeholder = { Text("Introduce tu contraseña") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Password"
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = { onIntent(AuthIntent.ToggleLoginPasswordVisibility) }
                ) {
                    Icon(
                        imageVector = if (state.isLoginPasswordVisible)
                            Icons.Default.VisibilityOff
                        else
                            Icons.Default.Visibility,
                        contentDescription = if (state.isLoginPasswordVisible)
                            "Ocultar contraseña"
                        else
                            "Mostrar contraseña"
                    )
                }
            },
            visualTransformation = if (state.isLoginPasswordVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botón de iniciar sesión
        Button(
            onClick = { onIntent(AuthIntent.SubmitLogin) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !state.isLoading
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(
                    text = "Iniciar Sesión",
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Enlace para ir a registro
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "¿No tienes cuenta? ",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(
                onClick = { onIntent(AuthIntent.NavigateToRegister) }
            ) {
                Text(
                    text = "Regístrate",
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Mostrar error si existe
        state.errorMessage?.let { error ->
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    ProyectoTheme {
        LoginScreen(
            state = AuthState(),
            onIntent = {}
        )
    }
}
