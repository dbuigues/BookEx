package es.rafapuig.pmdm.compose.proyecto.presentation.reviews

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import es.rafapuig.pmdm.compose.proyecto.R
import es.rafapuig.pmdm.compose.proyecto.domain.model.Review
import org.koin.androidx.compose.koinViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewsScreen(
    modifier: Modifier = Modifier,
    viewModel: ReviewsViewModel = koinViewModel(),
    onBookClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf<Review?>(null) }
    var showReviewDetail by remember { mutableStateOf<Review?>(null) }

    // Recargar las reseñas cada vez que la pantalla se hace visible
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.onAction(ReviewsIntent.LoadReviews)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Reseñas") }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Imagen de fondo
            Image(
                painter = painterResource(id = R.drawable.wallpaper_2),
                contentDescription = "Fondo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.15f
            )

            Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(50.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Cargando tus reseñas...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                uiState.errorMessage != null -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.RateReview,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = uiState.errorMessage ?: "Error desconocido",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.onAction(ReviewsIntent.LoadReviews) }) {
                            Text("Reintentar")
                        }
                    }
                }

                uiState.reviews.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.RateReview,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No tienes reseñas",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Busca un libro y añádelo a tu lista de Reviews",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        items(uiState.reviews) { review ->
                            ReviewCard(
                                review = review,
                                onClick = { showReviewDetail = review },
                                onDeleteClick = { showDeleteDialog = review }
                            )
                        }
                    }
                }
            }
            }
        }
    }

    // Diálogo de confirmación para eliminar
    showDeleteDialog?.let { review ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Eliminar reseña") },
            text = { Text("¿Estás seguro de que quieres eliminar la reseña de \"${review.bookTitle}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onAction(ReviewsIntent.DeleteReview(review.id))
                        showDeleteDialog = null
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo para mostrar la reseña completa
    showReviewDetail?.let { review ->
        ReviewDetailDialog(
            review = review,
            onDismiss = { showReviewDetail = null }
        )
    }
}

@Composable
fun ReviewCard(
    review: Review,
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
                model = review.bookCoverUrl,
                contentDescription = review.bookTitle,
                modifier = Modifier
                    .width(80.dp)
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Información de la reseña
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = review.bookTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = review.bookAuthor,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Estrellas de puntuación
                RatingStars(rating = review.rating)

                Spacer(modifier = Modifier.height(8.dp))

                // Texto de la reseña
                Text(
                    text = review.reviewText,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                // Fecha
                review.publishedDate?.let { date ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = date,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Botón eliminar
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar reseña",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun RatingStars(rating: Int, maxRating: Int = 5) {
    Row {
        repeat(maxRating) { index ->
            Icon(
                imageVector = if (index < rating) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = null,
                tint = if (index < rating) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun ReviewDetailDialog(
    review: Review,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    // Función para compartir la reseña
    val shareReview = {
        val starsEmoji = "⭐".repeat(review.rating)
        val shareText = buildString {
            appendLine("📚 reseña en BookEx.")
            appendLine()
            appendLine("Libro: ${review.bookTitle}")
            appendLine("Autor: ${review.bookAuthor}")
            appendLine("Puntuación: $starsEmoji (${review.rating}/5)")
            appendLine()
            appendLine("Mi opinión:")
            appendLine(review.reviewText)
            appendLine()
            appendLine("Compartido desde BookEx")
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, "Compartir reseña")
        context.startActivity(shareIntent)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = review.bookTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "por ${review.bookAuthor}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                // Portada y puntuación
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    // Portada
                    AsyncImage(
                        model = review.bookCoverUrl,
                        contentDescription = review.bookTitle,
                        modifier = Modifier
                            .width(80.dp)
                            .height(120.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "Tu puntuación",
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row {
                            repeat(5) { index ->
                                Icon(
                                    imageVector = if (index < review.rating) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = null,
                                    tint = if (index < review.rating) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        review.publishedDate?.let { date ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Publicada: $date",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider()

                Spacer(modifier = Modifier.height(16.dp))

                // Título de la reseña
                Text(
                    text = "Tu reseña",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Texto completo de la reseña con scroll
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = review.reviewText,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        confirmButton = {
            Row {
                // Botón de compartir
                TextButton(
                    onClick = shareReview
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Compartir",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Compartir")
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Botón de cerrar
                TextButton(onClick = onDismiss) {
                    Text("Cerrar")
                }
            }
        }
    )
}

