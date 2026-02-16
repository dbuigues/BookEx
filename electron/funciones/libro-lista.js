// libro-lista.js
// Funcionalidad para guardar libros en listas del usuario

const API_BASE_URL = 'https://bookex-u97b.onrender.com/api';
const API_KEY = "AIzaSyAA8hrOze05X9GGh9KbKBuRd4Lt_9zCAt0";

// Obtener el ID del libro de Google Books desde la URL
function getBookId() {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get('id');
}

// Obtener el ID de la lista desde la URL
function getListaId() {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get('idLista');
}

// Obtener la sesión activa del usuario
function getSesionActiva() {
    return sessionStorage.getItem('sesionActiva');
}

function mostrarAlerta(mensaje) {
  return new Promise((resolve) => {
    const dialog = document.createElement('dialog');
    dialog.classList.add('alerta-centrada');
    dialog.innerHTML = `
      <div style="padding: 20px;">
        <p>${mensaje}</p>
        <button id="ok">OK</button>
      </div>
    `;
    document.body.appendChild(dialog);
    dialog.showModal();
    
    dialog.querySelector('#ok').addEventListener('click', () => {
      dialog.close();
      dialog.remove();
      resolve();
    });
  });
}

// Obtener las listas del usuario
async function obtenerListasUsuario(correo) {
    try {
        const response = await fetch(`${API_BASE_URL}/listas/usuario/bycorreo/${encodeURIComponent(correo)}`);
        if (!response.ok) {
            throw new Error('Error al obtener las listas');
        }
        const data = await response.json();
        return Array.isArray(data) ? data : (data.items || []);
    } catch (error) {
        console.error('Error al obtener listas:', error);
        return [];
    }
}

// Verificar si el libro ya está en la lista y devolver el libro encontrado
async function buscarLibroEnLista(idLista, bookId) {
    try {
        const response = await fetch(`${API_BASE_URL}/libros-listas/lista/${idLista}`);
        if (!response.ok) {
            return null;
        }
        const libros = await response.json();
        const librosArray = Array.isArray(libros) ? libros : [];
        return librosArray.find(libro => libro.googleBookId === bookId) || null;
    } catch (error) {
        console.error('Error al verificar libro en lista:', error);
        return null;
    }
}

// Agregar libro a una lista
async function agregarLibroALista(idLista, bookId) {
    try {
        // Verificar si el libro ya está en la lista
        const libroExistente = await buscarLibroEnLista(idLista, bookId);
        if (libroExistente) {
            mostrarAlerta('Este libro ya está en la lista seleccionada');
            return false;
        }

        // Crear el objeto LibroListaDTO para enviar al API
        const libroLista = {
            idLista: idLista,
            googleBookId: bookId,
            resena: null,
            puntuacion: null,
            fechaPublicacion: null
        };

        // Agregar el libro a la lista usando el endpoint correcto
        const response = await fetch(`${API_BASE_URL}/libros-listas`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(libroLista)
        });

        if (response.ok) {
            return true;
        } else {
            const errorText = await response.text();
            console.error('Error del servidor:', errorText);
            return false;
        }
    } catch (error) {
        console.error('Error al agregar libro a la lista:', error);
        return false;
    }
}

// Mostrar modal con las listas disponibles
async function mostrarModalListas() {
    const sesion = getSesionActiva();
    if (!sesion) {
        mostrarAlerta('Debes iniciar sesión para guardar libros en tus listas');
        window.close(); 
        return;
    }

    const listas = await obtenerListasUsuario(sesion);
    if (listas.length === 0) {
        mostrarAlerta('No tienes listas creadas. Crea una lista primero en la página "Mis listas".');
        return;
    }

    const modal = document.getElementById('listasModal');
    const listasContainer = document.getElementById('listasSeleccion');
    
    // Limpiar el contenedor
    listasContainer.innerHTML = '';

    // Crear elementos para cada lista
    listas.forEach(lista => {
        const listaItem = document.createElement('div');
        listaItem.className = 'lista-selectable';
        listaItem.textContent = lista.nombreLista;
        listaItem.onclick = async () => {
            const bookId = getBookId();
            const exito = await agregarLibroALista(lista.idLista, bookId);
            if (exito) {
                mostrarAlerta(`Libro guardado en la lista "${lista.nombreLista}"`);
                modal.style.display = 'none';
            } else {
                mostrarAlerta('Error al guardar el libro. Intenta nuevamente.');
            }
        };
        listasContainer.appendChild(listaItem);
    });

    modal.style.display = 'flex';
}



async function fetchBookData(id) {
    try {
        console.log(id);
        const response = await fetch(`https://www.googleapis.com/books/v1/volumes/${id}?key=${API_KEY}`);
        const data = await response.json();
        if (data && data.volumeInfo) {
            const book = data.volumeInfo;
            return {
                title: book.title || 'Título no disponible',
                author: book.authors ? book.authors.join(', ') : 'Autor desconocido',
                genre: book.categories ? book.categories.join(', ') : 'Género desconocido',
                cover: book.imageLinks?.thumbnail || '../assets/imagenes/logo.png',
                reviews: book.ratingsCount || 'Sin reseñas'
            };
        }
        return null;
    } catch (error) {
        console.error('Error al obtener datos del libro:', error);
        return null;
    }
}

async function loadBookDetail() {
    const id = getBookId();
    if (!id) {
        document.getElementById('bookTitle').textContent = 'Libro no encontrado';
        return;
    }
    const book = await fetchBookData(id);
    if (!book) {
        document.getElementById('bookTitle').textContent = 'No se encontraron datos para este libro';
        return;
    }
    document.getElementById('bookTitle').textContent = book.title;
    document.getElementById('bookAuthor').textContent = book.author;
    document.getElementById('bookGenre').textContent = book.genre;
    document.getElementById('bookCover').src = book.cover;
}

// Cerrar modal
function cerrarModal() {
    const modal = document.getElementById('listasModal');
    modal.style.display = 'none';
}

// Obtener la lista "Reviews" del usuario
async function obtenerListaReviews(correo) {
    const listas = await obtenerListasUsuario(correo);
    return listas.find(lista => lista.nombreLista === 'Reviews') || null;
}

// Guardar reseña desde el formulario
async function guardarResena() {
    const sesion = getSesionActiva();
    if (!sesion) {
        mostrarAlerta('Debes iniciar sesión para escribir una reseña');
        return;
    }

    const bookId = getBookId();
    if (!bookId) {
        mostrarAlerta('No se encontró el libro.');
        return;
    }

    // Obtener los valores del formulario
    const ratingElements = document.getElementsByName('rating');
    let puntuacion = null;
    for (const radio of ratingElements) {
        if (radio.checked) {
            puntuacion = parseInt(radio.value);
            break;
        }
    }

    const reviewText = document.getElementById('reviewText').value.trim();

    if (!puntuacion) {
        mostrarAlerta('Por favor, selecciona una puntuación con las estrellas');
        return;
    }

    if (!reviewText) {
        mostrarAlerta('Por favor, escribe tu reseña');
        return;
    }

    // Buscar la lista "Reviews" del usuario
    const listaReviews = await obtenerListaReviews(sesion);
    if (!listaReviews) {
        mostrarAlerta('No se encontró tu lista de Reviews. Contacta con soporte.');
        return;
    }

    const idLista = listaReviews.idLista;
    // Una sola llamada para buscar si el libro existe y obtenerlo
    const libroExistente = await buscarLibroEnLista(idLista, bookId);

    if (!libroExistente) {
        // Crear la entrada del libro en la lista Reviews con la reseña
        const libroLista = {
            idLista: idLista,
            googleBookId: bookId,
            resena: reviewText,
            puntuacion: puntuacion,
            fechaPublicacion: new Date().toISOString().split('T')[0]
        };

        const response = await fetch(`${API_BASE_URL}/libros-listas`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(libroLista)
        });

        if (response.ok) {
            mostrarAlerta('¡Reseña guardada exitosamente!');
        } else {
            mostrarAlerta('Error al guardar la reseña. Intenta nuevamente.');
        }
    } else {
        // El libro ya existe en Reviews, actualizar directamente (reutilizar objeto)
        libroExistente.resena = reviewText;
        libroExistente.puntuacion = puntuacion;
        libroExistente.fechaPublicacion = new Date().toISOString().split('T')[0];

        const updateResponse = await fetch(`${API_BASE_URL}/libros-listas/${libroExistente.id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(libroExistente)
        });

        if (updateResponse.ok) {
            mostrarAlerta('¡Reseña actualizada exitosamente!');
        } else {
            mostrarAlerta('Error al actualizar la reseña. Intenta nuevamente.');
        }
    }
}

// Event listeners
document.addEventListener('DOMContentLoaded', () => {
    loadBookDetail();
    const saveButton = document.getElementById('saveButton');
    const closeModalBtn = document.getElementById('cerrarModalListas');
    const submitReviewBtn = document.getElementById('submitReview');

    if (submitReviewBtn) {
        submitReviewBtn.addEventListener('click', guardarResena);
    }

    if (saveButton) {
        saveButton.addEventListener('click', mostrarModalListas);
    }

    if (closeModalBtn) {
        closeModalBtn.addEventListener('click', cerrarModal);
    }

    // Cerrar modal al hacer clic fuera de él
    window.addEventListener('click', (event) => {
        const modal = document.getElementById('listasModal');
        if (event.target === modal) {
            cerrarModal();
        }
    });
});
