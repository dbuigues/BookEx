// libro-lista.js
// Funcionalidad para guardar libros en listas del usuario

const API_BASE_URL = 'https://bookex-u97b.onrender.com/api';

// Obtener el ID del libro de Google Books desde la URL
function getBookId() {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get('id');
}

// Obtener la sesión activa del usuario
function getSesionActiva() {
    return sessionStorage.getItem('sesionActiva');
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

// Verificar si el libro ya está en la lista
async function libroYaEnLista(idLista, bookId) {
    try {
        const response = await fetch(`${API_BASE_URL}/libros-listas/lista/${idLista}`);
        if (!response.ok) {
            return false;
        }
        const libros = await response.json();
        const librosArray = Array.isArray(libros) ? libros : [];
        return librosArray.some(libro => libro.googleBookId === bookId);
    } catch (error) {
        console.error('Error al verificar libro en lista:', error);
        return false;
    }
}

// Agregar libro a una lista
async function agregarLibroALista(idLista, bookId) {
    try {
        // Verificar si el libro ya está en la lista
        const yaExiste = await libroYaEnLista(idLista, bookId);
        if (yaExiste) {
            alert('Este libro ya está en la lista seleccionada');
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
        alert('Debes iniciar sesión para guardar libros en tus listas');
        window.close(); 
        return;
    }

    const listas = await obtenerListasUsuario(sesion);
    if (listas.length === 0) {
        alert('No tienes listas creadas. Crea una lista primero en la página "Mis listas".');
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
                alert(`Libro guardado en la lista "${lista.nombreLista}"`);
                modal.style.display = 'none';
            } else {
                alert('Error al guardar el libro. Intenta nuevamente.');
            }
        };
        listasContainer.appendChild(listaItem);
    });

    modal.style.display = 'flex';
}

// Cerrar modal
function cerrarModal() {
    const modal = document.getElementById('listasModal');
    modal.style.display = 'none';
}

// Event listeners
document.addEventListener('DOMContentLoaded', () => {
    const saveButton = document.getElementById('saveButton');
    const closeModalBtn = document.getElementById('cerrarModalListas');

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
