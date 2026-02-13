// showReviews.js
// Muestra las reseñas del usuario desde su lista "Reviews"

const API_BASE = 'https://bookex-u97b.onrender.com/api';
const GOOGLE_BOOKS_KEY = 'AIzaSyAA8hrOze05X9GGh9KbKBuRd4Lt_9zCAt0';

// Pre-calentar servidor
fetch(`${API_BASE}/usuarios`, { method: 'HEAD' }).catch(() => {});

async function cargarResenasUsuario() {
    const correo = sessionStorage.getItem('sesionActiva');
    const container = document.getElementById('resenas-container');

    if (!correo) {
        container.innerHTML = '<p class="empty-message">Inicia sesión para ver tus reseñas.</p>';
        return;
    }

    container.innerHTML = '<p class="loading-text">Cargando tus reseñas...</p>';

    try {
        // Obtener listas del usuario y buscar la de "Reviews"
        const response = await fetch(`${API_BASE}/listas/usuario/bycorreo/${encodeURIComponent(correo)}`);
        if (!response.ok) throw new Error('Error al obtener listas');
        const listas = await response.json();
        const listaReviews = listas.find(l => l.nombreLista === 'Reviews');

        if (!listaReviews) {
            container.innerHTML = '<p class="empty-message">No tienes una lista de reseñas.</p>';
            return;
        }

        // Obtener los libros de la lista Reviews
        const librosResponse = await fetch(`${API_BASE}/libros-listas/lista/${listaReviews.idLista}`);
        if (!librosResponse.ok) throw new Error('Error al obtener libros');
        const libros = await librosResponse.json();

        // Filtrar solo los que tienen reseña
        const resenados = libros.filter(libro => libro.resena && libro.resena.trim() !== '');

        if (resenados.length === 0) {
            container.innerHTML = '<p class="empty-message">Aún no has escrito ninguna reseña.</p>';
            return;
        }

        // Obtener info de todos los libros en paralelo
        const infoPromises = resenados.map(libro =>
            fetch(`https://www.googleapis.com/books/v1/volumes/${libro.googleBookId}?key=${GOOGLE_BOOKS_KEY}`)
                .then(r => r.json())
                .catch(() => null)
        );
        const infos = await Promise.all(infoPromises);

        container.innerHTML = '';

        resenados.forEach((libro, i) => {
            const data = infos[i]?.volumeInfo;
            const titulo = data?.title || 'Título no disponible';
            const autor = data?.authors?.join(', ') || 'Autor desconocido';
            const portada = data?.imageLinks?.thumbnail || '../assets/imagenes/logo.png';

            const estrellas = '★'.repeat(libro.puntuacion || 0) + '☆'.repeat(5 - (libro.puntuacion || 0));
            const fecha = libro.fechaPublicacion || 'Sin fecha';

            const card = document.createElement('div');
            card.className = 'review-card';
            card.innerHTML = `
                <img src="${portada}" alt="${titulo}" class="review-cover">
                <div class="review-content">
                    <h3 class="review-title">${titulo}</h3>
                    <p class="review-author">${autor}</p>
                    <div class="review-rating">${estrellas}</div>
                    <p class="review-text">${libro.resena}</p>
                    <p class="review-date">${fecha}</p>
                </div>
            `;

            card.style.cursor = 'pointer';
            card.onclick = () => {
                window.location.href = `BookDetail.html?id=${libro.googleBookId}`;
            };

            container.appendChild(card);
        });

    } catch (error) {
        console.error('Error al cargar reseñas:', error);
        container.innerHTML = '<p class="empty-message">Error al cargar las reseñas. Intenta de nuevo.</p>';
    }
}

document.addEventListener('DOMContentLoaded', cargarResenasUsuario);
