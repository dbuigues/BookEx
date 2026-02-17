const BotonAgregarLista = document.getElementById('agregar-lista');
const BotoncerrarModal = document.getElementById('cerrar');
const listasContainer = document.getElementById('listas-container');
const modal = document.getElementById('modal');
const BotonCrearLista = document.getElementById('crear-lista');
let sesion;

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

// Obtener libros de una lista
const obtenerLibrosLista = async (idLista) => {
  try {
    const response = await fetch(`https://bookex-u97b.onrender.com/api/libros-listas/lista/${idLista}`);
    if (!response.ok) {
      throw new Error('Error al obtener libros');
    }
    const libros = await response.json();
    return Array.isArray(libros) ? libros : [];
  } catch (error) {
    console.error('Error al obtener libros de la lista:', error);
    return [];
  }
};

// Obtener información del libro desde Google Books
const obtenerInfoLibro = async (googleBookId) => {
  try {
    const response = await fetch(`https://www.googleapis.com/books/v1/volumes/${googleBookId}?key=AIzaSyAA8hrOze05X9GGh9KbKBuRd4Lt_9zCAt0`);
    if (!response.ok) return null;
    const data = await response.json();
    if (data && data.volumeInfo) {
      const book = data.volumeInfo;
      let thumbnail = '../assets/imagenes/logo.png';
      if (book.imageLinks && book.imageLinks.thumbnail) {
        if (window.getCachedImageIfAvailable) {
          thumbnail = await window.getCachedImageIfAvailable(book.imageLinks.thumbnail);
        } else {
          thumbnail = book.imageLinks.thumbnail;
        }
      }
      return {
        title: book.title || 'Título no disponible',
        author: book.authors ? book.authors.join(', ') : 'Autor desconocido',
        thumbnail: thumbnail
      };
    }
    return null;
  } catch (error) {
    console.error('Error al obtener info del libro:', error);
    return null;
  }
};

// Eliminar libro de una lista
const eliminarLibroLista = async (idLibroLista, tituloLibro, idLista) => {
  if (!confirm(`¿Estás seguro de que deseas eliminar "${tituloLibro}" de esta lista?`)) {
    return;
  }

  try {
    const response = await fetch(`https://bookex-u97b.onrender.com/api/libros-listas/${idLibroLista}`, {
      method: 'DELETE'
    });

    if (response.ok) {
      mostrarAlerta('Libro eliminado de la lista exitosamente');
      // Recargar los libros de la lista
      const librosContainer = document.querySelector(`[data-lista-id="${idLista}"]`);
      if (librosContainer) {
        await toggleLibrosLista(idLista, librosContainer, true);
      }
    } else {
      mostrarAlerta('Error al eliminar el libro de la lista');
    }
  } catch (error) {
    console.error('Error al eliminar libro de la lista:', error);
    mostrarAlerta('Error al eliminar el libro de la lista');
  }
};

// Mostrar/ocultar libros de una lista
const toggleLibrosLista = async (idLista, librosContainer, forceOpen = false) => {
  if (librosContainer.style.display === 'block' && !forceOpen) {
    librosContainer.style.display = 'none';
    return;
  }

  librosContainer.innerHTML = '<p class="loading-text">Cargando libros...</p>';
  librosContainer.style.display = 'block';
  librosContainer.setAttribute('data-lista-id', idLista);

  const libros = await obtenerLibrosLista(idLista);
  
  if (libros.length === 0) {
    librosContainer.innerHTML = '<p class="empty-list">No hay libros en esta lista</p>';
    return;
  }

  librosContainer.innerHTML = '';
  
  for (const libro of libros) {
    const infoLibro = await obtenerInfoLibro(libro.googleBookId);
    if (infoLibro) {
      const libroDiv = document.createElement('div');
      libroDiv.className = 'libro-item';
      
      const libroContent = document.createElement('div');
      libroContent.className = 'libro-content';
      libroContent.innerHTML = `
        <img src="${infoLibro.thumbnail}" alt="${infoLibro.title}" class="libro-thumbnail">
        <div class="libro-info">
          <h4>${infoLibro.title}</h4>
          <p>${infoLibro.author}</p>
          ${libro.puntuacion ? `<p class="puntuacion">⭐ ${libro.puntuacion}/5</p>` : ''}
        </div>
      `;
      libroContent.onclick = () => {
        window.location.href = `BookDetail.html?id=${libro.googleBookId}`;
      };
      
      const btnEliminar = document.createElement('button');
      btnEliminar.className = 'btn-eliminar-lista';
      btnEliminar.innerHTML = 'Eliminar';
      btnEliminar.onclick = (e) => {
        e.stopPropagation();
        eliminarLibroLista(libro.id, infoLibro.title, idLista);
      };
      
      libroDiv.appendChild(libroContent);
      libroDiv.appendChild(btnEliminar);
      librosContainer.appendChild(libroDiv);
    }
  }
};

// Eliminar lista
const eliminarLista = async (idLista, nombreLista) => {
  if (!confirm(`¿Estás seguro de que deseas eliminar la lista "${nombreLista}"?`)) {
    return;
  }

  try {
    const response = await fetch(`https://bookex-u97b.onrender.com/api/listas/${idLista}`, {
      method: 'DELETE'
    });

    if (response.ok) {
      mostrarAlerta('Lista eliminada exitosamente');
      verListas(sesion);
    } else {
      mostrarAlerta('Error al eliminar la lista');
    }
  } catch (error) {
    console.error('Error al eliminar lista:', error);
    mostrarAlerta('Error al eliminar la lista');
  }
};

const verListas = async (sesion) => {
  try {
    const response = await fetch(`https://bookex-u97b.onrender.com/api/listas/usuario/bycorreo/${encodeURIComponent(sesion)}`);
    const data = await response.json();
    console.log("Datos recibidos:", data);
    console.log("Cargando listas...");
    
    const container = document.getElementById('listas-container');
    if (!container) {
      console.error("No se encontró el elemento listas-container");
      return;
    }
    
    container.innerHTML = '';
    
    // Verificar si data es un array directamente o tiene una propiedad items
    const listas = Array.isArray(data) ? data : (data.items || []);
    console.log("Listas a mostrar:", listas);
    
    if (listas.length > 0) {
      listas.forEach(lista => {
        const listaWrapper = document.createElement('div');
        listaWrapper.className = 'lista-wrapper';

        const listaHeader = document.createElement('div');
        listaHeader.className = 'lista-header';

        const listaNombre = document.createElement('span');
        listaNombre.className = 'lista-nombre';
        listaNombre.textContent = lista.nombreLista;

        const botonesContainer = document.createElement('div');
        botonesContainer.className = 'lista-botones';

        // No mostrar botón eliminar para listas protegidas (Favoritos y Reviews)
        const listasProtegidas = ['Favoritos', 'Reviews'];
        if (!listasProtegidas.includes(lista.nombreLista)) {
          const botonEliminar = document.createElement('button');
          botonEliminar.className = 'btn-eliminar-lista';
          botonEliminar.innerHTML = 'Eliminar';
          botonEliminar.onclick = (e) => {
            e.stopPropagation();
            eliminarLista(lista.idLista, lista.nombreLista);
          };

          botonesContainer.appendChild(botonEliminar);
        }
        listaHeader.appendChild(listaNombre);
        listaHeader.appendChild(botonesContainer);

        const librosContainer = document.createElement('div');
        librosContainer.className = 'libros-container';
        librosContainer.style.display = 'none';

        listaHeader.onclick = () => {
          toggleLibrosLista(lista.idLista, librosContainer);
        };

        listaWrapper.appendChild(listaHeader);
        listaWrapper.appendChild(librosContainer);
        container.appendChild(listaWrapper);
      });
    } else {
      container.innerHTML = '<p class="empty-message">No tienes listas creadas aún</p>';
    }
  } catch (error) {
    console.error(`Error al buscar listas del usuario:`, error);
    const container = document.getElementById('listas-container');
    if (container) {
      container.innerHTML = '<p>Error al cargar las listas.</p>';
    }
  }
}

const crearLista = async (nombreDeLista, sesion) => {

  try {
    const idCall = await fetch(`https://bookex-u97b.onrender.com/api/usuarios/correo/${encodeURIComponent(sesion)}`);
    const userData = await idCall.json();
    const id = userData.idUsuario;
    console.log("Datos recibidos para crear lista:", id);

    const lista = {
      nombreLista: nombreDeLista, 
      idUsuario: id
    };
    const response = await fetch('https://bookex-u97b.onrender.com/api/listas', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(lista)
    });
    if (response.ok) {
      console.log("Lista creada exitosamente");
      modal.style.display = 'none';
      verListas(sesion);
    }
  } catch (error) {
    console.error('Error al crear la lista:', error);
  }
}


if (sessionStorage.getItem('sesionActiva')!=null) {
  sesion = sessionStorage.getItem('sesionActiva');
  BotonAgregarLista.style.display = 'block';
  verListas(sesion);
}else{
  BotonAgregarLista.style.display = 'none';
}

BotonAgregarLista.addEventListener('click', () => { modal.style.display = 'block'; });
BotoncerrarModal.addEventListener('click', () => { modal.style.display = 'none'; });
BotonCrearLista.addEventListener('click', (event) => {
  event.preventDefault();
  crearLista(document.getElementById('nombre-lista').value, sesion);
});
