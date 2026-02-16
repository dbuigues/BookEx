package es.rafapuig.pmdm.compose.proyecto.presentation.lists

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import es.rafapuig.pmdm.compose.proyecto.data.repository.ListsRemoteRepositoryImpl
import es.rafapuig.pmdm.compose.proyecto.data.remote.RetrofitClient
import es.rafapuig.pmdm.compose.proyecto.data.remote.api.ListaApiService
import es.rafapuig.pmdm.compose.proyecto.data.remote.api.LibroListaApiService
import es.rafapuig.pmdm.compose.proyecto.ui.theme.ProyectoTheme
import es.rafapuig.pmdm.compose.proyecto.data.local.TokenManager
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListsScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val ownerId = tokenManager.getUserId().takeIf { it >= 0 } ?: -1L

    // crear servicios retrofit y repositorio remoto
    val listaService = remember { RetrofitClient.createService(ListaApiService::class.java) }
    val libroListaService = remember { RetrofitClient.createService(LibroListaApiService::class.java) }
    val repo = remember { ListsRemoteRepositoryImpl(listaService, libroListaService) }
    val viewModel = remember { ListsViewModel(repo, ownerId) }

    val lists by viewModel.lists.collectAsState()
    val error by viewModel.error.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var newListName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Listas") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Crear")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)) {

                // Encabezado grande
                Text(
                    text = "MIS LISTAS",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (error != null) {
                    Text(text = error ?: "", color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Listado de listas
                if (lists.isEmpty()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.List,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Aún no tienes listas",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(text = "Pulsa el botón + para crear una nueva lista")
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(lists) { lista ->
                            Card(modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable { /* abrir detalle de lista si se desea */ }) {
                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = lista.name, fontWeight = FontWeight.Bold)
                                        Text(text = "${lista.items.size} libros", style = MaterialTheme.typography.bodySmall)
                                    }
                                    Button(onClick = { viewModel.deleteList(lista.id) }) {
                                        Text("Eliminar")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Dialogo para crear lista
            if (showCreateDialog) {
                AlertDialog(
                    onDismissRequest = { showCreateDialog = false },
                    title = { Text("Nueva lista") },
                    text = {
                        Column {
                            OutlinedTextField(
                                value = newListName,
                                onValueChange = { newListName = it },
                                label = { Text("Nombre de la lista") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            if (newListName.isBlank()) {
                                // mostrar error simple
                                viewModel.createList(newListName)
                                // si está en blanco createList lo manejará, pero mejor prevenir
                            } else {
                                viewModel.createList(newListName.trim())
                            }
                            newListName = ""
                            showCreateDialog = false
                        }) { Text("Crear") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showCreateDialog = false }) { Text("Cancelar") }
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ListsScreenPreview() {
    ProyectoTheme {
        ListsScreen()
    }
}
