package es.rafapuig.pmdm.compose.proyecto.presentation.lists

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import es.rafapuig.pmdm.compose.proyecto.data.repository.ListsRemoteRepositoryImpl
import es.rafapuig.pmdm.compose.proyecto.data.remote.RetrofitClient
import es.rafapuig.pmdm.compose.proyecto.data.remote.api.BookApiService
import es.rafapuig.pmdm.compose.proyecto.data.remote.api.ListaApiService
import es.rafapuig.pmdm.compose.proyecto.data.remote.api.LibroListaApiService
import es.rafapuig.pmdm.compose.proyecto.domain.model.Book
import es.rafapuig.pmdm.compose.proyecto.domain.model.ListBook
import es.rafapuig.pmdm.compose.proyecto.ui.theme.ProyectoTheme
import es.rafapuig.pmdm.compose.proyecto.data.local.TokenManager
import es.rafapuig.pmdm.compose.proyecto.presentation.home.BookDetailDialog
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListsScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val ownerId = tokenManager.getUserId().takeIf { it >= 0 } ?: -1L
    val coroutineScope = rememberCoroutineScope()

    // Obtener el servicio de libros para cargar detalles
    val bookApiService = remember { RetrofitClient.createService(BookApiService::class.java) }

    val viewModel: ListsViewModel = org.koin.androidx.compose.koinViewModel(parameters = { org.koin.core.parameter.parametersOf(ownerId) })

    val isLoading by viewModel.isLoading.collectAsState()
    val lists by viewModel.lists.collectAsState()
    val error by viewModel.error.collectAsState()

    // Estado para lista seleccionada
    val selectedList by viewModel.selectedList.collectAsState()
    val booksInList by viewModel.booksInList.collectAsState()
    val isLoadingBooks by viewModel.isLoadingBooks.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var newListName by remember { mutableStateOf("") }
    var bookToDelete by remember { mutableStateOf<ListBook?>(null) }
    var selectedBookForDetail by remember { mutableStateOf<Book?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(selectedList?.name ?: "Mis Listas")
                },
                navigationIcon = {
                    if (selectedList != null) {
                        IconButton(onClick = { viewModel.clearSelectedList() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver"
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (selectedList == null) {
                FloatingActionButton(onClick = { showCreateDialog = true }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Crear")
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            // Mostrar detalle de lista o listado de listas
            if (selectedList != null) {
                // Vista de detalle de lista con sus libros
                ListDetailContent(
                    books = booksInList,
                    isLoading = isLoadingBooks,
                    error = error,
                    onBookClick = { listBook ->
                        // Obtener la información completa del libro desde la API
                        coroutineScope.launch {
                            try {
                                val response = bookApiService.getBookById(listBook.googleBookId)
                                if (response.isSuccessful && response.body() != null) {
                                    val bookDto = response.body()!!
                                    // Limpiar HTML de la descripción
                                    val cleanDescription = bookDto.descripcion
                                        ?.replace(Regex("<[^>]*>"), "") // Eliminar etiquetas HTML
                                        ?.replace("&nbsp;", " ")
                                        ?.replace("&amp;", "&")
                                        ?.replace("&lt;", "<")
                                        ?.replace("&gt;", ">")
                                        ?.replace("&quot;", "\"")
                                        ?.replace("&#39;", "'")
                                        ?.trim()
                                        ?: "Sin descripción disponible"

                                    selectedBookForDetail = Book(
                                        id = bookDto.googleBookId,
                                        title = bookDto.titulo,
                                        author = bookDto.autores?.joinToString(", ") ?: "Autor desconocido",
                                        description = cleanDescription,
                                        coverUrl = bookDto.imagenPortada ?: bookDto.imagenPequena,
                                        isbn = bookDto.isbn
                                    )
                                } else {
                                    // Fallback si falla la API
                                    selectedBookForDetail = Book(
                                        id = listBook.googleBookId,
                                        title = listBook.title,
                                        author = listBook.author,
                                        description = "No se pudo cargar la descripción",
                                        coverUrl = listBook.coverUrl,
                                        isbn = null
                                    )
                                }
                            } catch (e: Exception) {
                                // Fallback en caso de error
                                selectedBookForDetail = Book(
                                    id = listBook.googleBookId,
                                    title = listBook.title,
                                    author = listBook.author,
                                    description = "No se pudo cargar la descripción",
                                    coverUrl = listBook.coverUrl,
                                    isbn = null
                                )
                            }
                        }
                    },
                    onDeleteBook = { book -> bookToDelete = book }
                )
            } else {
                // Vista de listado de listas (filtrando la lista "reviews")
                ListsContent(
                    lists = lists.filter { it.name.lowercase() != "reviews" },
                    isLoading = isLoading,
                    error = error,
                    onListClick = { lista -> viewModel.selectList(lista) },
                    onDeleteList = { listId -> viewModel.deleteList(listId) }
                )
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
                                viewModel.createList(newListName)
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

            // Dialogo para confirmar eliminación de libro
            bookToDelete?.let { book ->
                AlertDialog(
                    onDismissRequest = { bookToDelete = null },
                    title = { Text("Eliminar libro") },
                    text = { Text("¿Estás seguro de que quieres eliminar \"${book.title}\" de esta lista?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.removeBookFromList(book.id)
                                bookToDelete = null
                            }
                        ) {
                            Text("Eliminar", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { bookToDelete = null }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }

    // Diálogo de detalles del libro
    selectedBookForDetail?.let { book ->
        BookDetailDialog(
            book = book,
            onDismiss = { selectedBookForDetail = null }
        )
    }
}

@Composable
private fun ListsContent(
    lists: List<es.rafapuig.pmdm.compose.proyecto.domain.model.BookList>,
    isLoading: Boolean,
    error: String?,
    onListClick: (es.rafapuig.pmdm.compose.proyecto.domain.model.BookList) -> Unit,
    onDeleteList: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        if (error != null) {
            Text(text = error, color = MaterialTheme.colorScheme.error)
        }

        if (isLoading) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(50.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else {
            if (lists.isEmpty()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
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
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable { onListClick(lista) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = lista.name, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = "Toca para ver los libros",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                // No permitir eliminar las listas por defecto
                                val isDefaultList = lista.name.equals("Favoritos", ignoreCase = true) ||
                                                   lista.name.equals("Reviews", ignoreCase = true)
                                if (!isDefaultList) {
                                    Button(onClick = { onDeleteList(lista.id) }) {
                                        Text("Eliminar")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ListDetailContent(
    books: List<ListBook>,
    isLoading: Boolean,
    error: String?,
    onBookClick: (ListBook) -> Unit,
    onDeleteBook: (ListBook) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (error != null) {
            Text(text = error, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (isLoading) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(50.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Cargando libros...",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else if (books.isEmpty()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Esta lista está vacía",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Busca un libro y añádelo a esta lista",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(books) { book ->
                    BookInListCard(
                        book = book,
                        onClick = { onBookClick(book) },
                        onDeleteClick = { onDeleteBook(book) }
                    )
                }
            }
        }
    }
}

@Composable
private fun BookInListCard(
    book: ListBook,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Portada del libro
            AsyncImage(
                model = book.coverUrl,
                contentDescription = book.title,
                modifier = Modifier
                    .width(70.dp)
                    .height(100.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Información del libro
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = book.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = book.author,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Mostrar reseña si existe
                book.review?.let { review ->
                    if (review.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = review,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Botón eliminar
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar de la lista",
                    tint = MaterialTheme.colorScheme.error
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
