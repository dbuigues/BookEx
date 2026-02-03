package es.rafapuig.pmdm.compose.proyecto.feature.auth.view

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import es.rafapuig.pmdm.compose.proyecto.feature.auth.intent.AuthIntent
import es.rafapuig.pmdm.compose.proyecto.feature.auth.model.AuthState
import es.rafapuig.pmdm.compose.proyecto.ui.theme.ProyectoTheme

/**
 * Pantalla de Registro siguiendo el patrón MVI
 * Recibe el estado y emite intents al ViewModel
 */
@Composable
fun RegisterScreen(
    state: AuthState,
    onIntent: (AuthIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // Launcher para seleccionar imagen de galería
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onIntent(AuthIntent.UpdateProfileImage(uri))
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Título
        Text(
            text = "Crear Cuenta",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Únete a la comunidad de BookEx",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Selector de imagen de perfil
        ProfileImagePicker(
            imageUri = state.profileImageUri,
            onImageClick = { imagePickerLauncher.launch("image/*") }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Campo de nombre de usuario
        OutlinedTextField(
            value = state.registerUsername,
            onValueChange = { onIntent(AuthIntent.UpdateRegisterUsername(it)) },
            label = { Text("Nombre de usuario") },
            placeholder = { Text("Tu nombre de usuario") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Usuario"
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de correo electrónico
        OutlinedTextField(
            value = state.registerEmail,
            onValueChange = { onIntent(AuthIntent.UpdateRegisterEmail(it)) },
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
            value = state.registerPassword,
            onValueChange = { onIntent(AuthIntent.UpdateRegisterPassword(it)) },
            label = { Text("Contraseña") },
            placeholder = { Text("Crea una contraseña segura") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Password"
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = { onIntent(AuthIntent.ToggleRegisterPasswordVisibility) }
                ) {
                    Icon(
                        imageVector = if (state.isRegisterPasswordVisible)
                            Icons.Default.VisibilityOff
                        else
                            Icons.Default.Visibility,
                        contentDescription = if (state.isRegisterPasswordVisible)
                            "Ocultar contraseña"
                        else
                            "Mostrar contraseña"
                    )
                }
            },
            visualTransformation = if (state.isRegisterPasswordVisible)
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

        // Botón de registro
        Button(
            onClick = { onIntent(AuthIntent.SubmitRegister) },
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
                    text = "Registrarse",
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Enlace para ir a login
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "¿Ya tienes cuenta? ",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(
                onClick = { onIntent(AuthIntent.NavigateToLogin) }
            ) {
                Text(
                    text = "Inicia Sesión",
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

        Spacer(modifier = Modifier.height(24.dp))
    }
}

/**
 * Componente para seleccionar la imagen de perfil
 */
@Composable
private fun ProfileImagePicker(
    imageUri: Uri?,
    onImageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .size(120.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            )
            .clickable { onImageClick() },
        contentAlignment = Alignment.Center
    ) {
        if (imageUri != null) {
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(context)
                        .data(imageUri)
                        .crossfade(true)
                        .build()
                ),
                contentDescription = "Foto de perfil",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.AddAPhoto,
                    contentDescription = "Añadir foto",
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Añadir foto",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    ProyectoTheme {
        RegisterScreen(
            state = AuthState(),
            onIntent = {}
        )
    }
}
