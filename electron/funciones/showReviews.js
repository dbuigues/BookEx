// showReviews.js
// Muestra las reseñas del usuario desde su lista "Reviews"

const API_BASE = 'https://bookex-u97b.onrender.com/api';
const GOOGLE_BOOKS_KEY = 'AIzaSyAA8hrOze05X9GGh9KbKBuRd4Lt_9zCAt0';

// Pre-calentar servidor
fetch(`${API_BASE}/usuarios`, { method: 'HEAD' }).catch(() => {});

// Confirmación no bloqueante (evita usar el confirm() nativo que puede bloquear en Electron)
function mostrarConfirmacion(mensaje) {
    return new Promise((resolve) => {
        const dialog = document.createElement('dialog');
        dialog.className = 'alerta-centrada';
        dialog.innerHTML = `
            <div style="padding: 20px; text-align: center;">
                <p style="margin-bottom: 18px;">${mensaje}</p>
                <div>
                    <button id="confirm-yes" style="padding:8px 18px; margin-right:10px; background:#e74c3c; color:white; border:none; border-radius:6px; cursor:pointer; font-weight:600;">Eliminar</button>
                    <button id="confirm-no" style="padding:8px 18px; background:#bdc3c7; color:#2c3e50; border:none; border-radius:6px; cursor:pointer;">Cancelar</button>
                </div>
            </div>
        `;
        document.body.appendChild(dialog);
        dialog.showModal();

        const yes = dialog.querySelector('#confirm-yes');
        const no = dialog.querySelector('#confirm-no');

        function cleanup(result) {
            try { dialog.close(); } catch (e) {}
            dialog.remove();
            resolve(result);
        }

        yes.addEventListener('click', () => cleanup(true), { once: true });
        no.addEventListener('click', () => cleanup(false), { once: true });
        dialog.addEventListener('cancel', () => cleanup(false), { once: true });
    });
}

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
                    <div class="review-footer">
                        <p class="review-date">${fecha}</p>
                        <button class="btn-eliminar-resena">Eliminar reseña</button>
                    </div>
                </div>
            `;

            const btnEliminar = card.querySelector('.btn-eliminar-resena');
            btnEliminar.addEventListener('click', (e) => {
                e.stopPropagation();
                eliminarResena(libro.id);
            });

            card.addEventListener('click', () => {
                window.location.href = `BookDetail.html?id=${libro.googleBookId}`;
            });

            container.appendChild(card);
        });

    } catch (error) {
        console.error('Error al cargar reseñas:', error);
        container.innerHTML = '<p class="empty-message">Error al cargar las reseñas. Intenta de nuevo.</p>';
    }
}

async function eliminarResena(idLibroLista) {
    const confirmado = await mostrarConfirmacion('¿Estás seguro de que quieres eliminar esta reseña?');
    if (!confirmado) return;

    try {
        const response = await fetch(`${API_BASE}/libros-listas/${idLibroLista}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({ message: 'Error desconocido' }));
            throw new Error(errorData.message || `Error ${response.status}`);
        }

        // Recargar las reseñas
        cargarResenasUsuario();
    } catch (error) {
        console.error('Error al eliminar reseña:', error);
        
        // Mostrar dialog de error
        const dialog = document.createElement('dialog');
        dialog.className = 'alerta-centrada';
        dialog.innerHTML = `
            <div style="padding: 30px; text-align: center;">
                <h2 style="color: #e74c3c; margin-bottom: 20px;">Error al eliminar</h2>
                <p style="margin-bottom: 30px; color: #2c3e50;">${error.message || 'No se pudo eliminar la reseña. Intenta de nuevo.'}</p>
                <button onclick="this.closest('dialog').close()" style="padding: 10px 30px; background: #3498db; color: white; border: none; border-radius: 6px; cursor: pointer; font-size: 14px; font-weight: 600;">Cerrar</button>
            </div>
        `;
        document.body.appendChild(dialog);
        dialog.showModal();
        
        dialog.addEventListener('close', () => {
            dialog.remove();
        });
    }
}

document.addEventListener('DOMContentLoaded', cargarResenasUsuario);
