package es.rafapuig.pmdm.compose.proyecto.feature.books.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import es.rafapuig.pmdm.compose.proyecto.domain.model.Book
import es.rafapuig.pmdm.compose.proyecto.feature.home.presentation.BookDetailDialog
import es.rafapuig.pmdm.compose.proyecto.ui.theme.ProyectoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BooksScreen(
    state: BooksState,
    snackbarHostState: SnackbarHostState,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onBookClick: (Book) -> Unit,
    onDismissBookDetail: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Libros") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Buscador
            OutlinedTextField(
                value = state.query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Buscar libros") },
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = onSearch) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Buscar")
                    }
                },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                    onSearch = { onSearch() }
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                state.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                state.hasError -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = state.error ?: "Error",
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(onClick = onSearch) { Text("Reintentar") }
                        }
                    }
                }

                state.query.isBlank() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Book,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Busca libros en la API",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Escribe un título, autor o palabra clave.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 12.dp)
                    ) {
                        items(state.results) { book ->
                            BookListItem(
                                book = book,
                                onClick = { onBookClick(book) }
                            )
                        }
                    }
                }
            }
        }

        state.selectedBook?.let { book ->
            BookDetailDialog(
                book = book,
                onDismiss = onDismissBookDetail
            )
        }
    }
}

@Composable
private fun BookListItem(
    book: Book,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(width = 70.dp, height = 100.dp)
            ) {
                if (!book.coverUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = book.coverUrl,
                        contentDescription = "Portada ${book.title}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = es.rafapuig.pmdm.compose.proyecto.R.drawable.logo),
                        error = painterResource(id = es.rafapuig.pmdm.compose.proyecto.R.drawable.logo)
                    )
                } else {
                    Image(
                        painter = painterResource(id = es.rafapuig.pmdm.compose.proyecto.R.drawable.logo),
                        contentDescription = "Sin portada",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(text = book.title, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = book.author, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = book.description,
                    maxLines = 3,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BooksScreenPreview() {
    ProyectoTheme {
        BooksScreen(
            state = BooksState(
                query = "harry potter",
                results = listOf(
                    Book(
                        id = "1",
                        title = "Libro de ejemplo",
                        author = "Autor",
                        description = "Descripción de ejemplo",
                        coverUrl = null,
                        isbn = "123"
                    )
                )
            ),
            snackbarHostState = SnackbarHostState(),
            onQueryChange = {},
            onSearch = {},
            onBookClick = {},
            onDismissBookDetail = {}
        )
    }
}
