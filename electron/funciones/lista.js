// Código mínimo para crear listas con búsqueda y selección de libros

document.addEventListener('DOMContentLoaded', () => {
  const agregarBtn = document.getElementById('agregar-lista');
  const modal = document.getElementById('modal');
  const cerrar = document.getElementById('cerrar');
  const form = document.getElementById('form-nueva-lista');
  const listasContainer = document.getElementById('listas-container');

  // Elementos añadidos dinámicamente al modal
  let searchInput, resultsContainer, selectedContainer;
  let selectedBooks = []; // {id, title, author, thumbnail}

  function ensureSearchElements() {
    if (searchInput) return;

    // Crear campo de búsqueda
    searchInput = document.createElement('input');
    searchInput.type = 'search';
    searchInput.placeholder = 'Buscar libros para añadir a la lista...';
    searchInput.id = 'search-books-input';
    searchInput.style.width = '100%';
    searchInput.style.margin = '10px 0';

    // Contenedor de resultados
    resultsContainer = document.createElement('div');
    resultsContainer.id = 'search-results';
    resultsContainer.style.maxHeight = '200px';
    resultsContainer.style.overflowY = 'auto';
    resultsContainer.style.marginBottom = '10px';

    // Contenedor de seleccionados
    selectedContainer = document.createElement('div');
    selectedContainer.id = 'selected-books';
    selectedContainer.style.marginBottom = '10px';

    // Insertar antes del botón crear en el formulario
    const modalContent = modal.querySelector('.modal-content');
    const formEl = document.getElementById('form-nueva-lista');
    modalContent.insertBefore(searchInput, formEl);
    modalContent.insertBefore(resultsContainer, formEl);
    modalContent.insertBefore(selectedContainer, formEl);

    setupSearch();
  }

  function setupSearch() {
    let debounce;
    searchInput.addEventListener('input', () => {
      clearTimeout(debounce);
      debounce = setTimeout(async () => {
        const q = searchInput.value.trim();
        resultsContainer.innerHTML = '';
        if (!q) return;
        resultsContainer.innerHTML = '<p>Buscando...</p>';
        const books = await fetchBooks(q);
        resultsContainer.innerHTML = '';
        if (!books.length) {
          resultsContainer.innerHTML = '<p>No se encontraron resultados.</p>';
          return;
        }
        books.forEach(b => resultsContainer.appendChild(createResultItem(b)));
      }, 400);
    });
  }

  async function fetchBooks(query) {
    try {
      const res = await fetch(`https://www.googleapis.com/books/v1/volumes?q=${encodeURIComponent(query)}`);
      const data = await res.json();
      if (!data.items) return [];
      return data.items.map(item => {
        const v = item.volumeInfo || {};
        return {
          id: item.id,
          title: v.title || 'Título no disponible',
          author: v.authors ? v.authors.join(', ') : 'Autor desconocido',
          thumbnail: (v.imageLinks && v.imageLinks.thumbnail) ? v.imageLinks.thumbnail : '../assets/imagenes/placeholder.png'
        };
      });
    } catch (e) {
      console.error('Error buscando libros', e);
      return [];
    }
  }

  function createResultItem(book) {
    const el = document.createElement('div');
    el.className = 'book-result-item';
    el.style.display = 'flex';
    el.style.alignItems = 'center';
    el.style.gap = '10px';
    el.style.padding = '6px 0';
    el.style.borderBottom = '1px solid #eee';

    const cb = document.createElement('input');
    cb.type = 'checkbox';
    cb.dataset.bookId = book.id;

    // marcar si ya está seleccionado
    if (selectedBooks.find(b => b.id === book.id)) cb.checked = true;

    cb.addEventListener('change', () => {
      if (cb.checked) addSelectedBook(book);
      else removeSelectedBook(book.id);
    });

    const img = document.createElement('img');
    img.src = book.thumbnail;
    img.alt = book.title;
    img.style.width = '40px';
    img.style.height = '60px';
    img.style.objectFit = 'cover';

    const info = document.createElement('div');
    info.innerHTML = `<strong>${book.title}</strong><br><small>${book.author}</small>`;

    el.appendChild(cb);
    el.appendChild(img);
    el.appendChild(info);
    return el;
  }

  function addSelectedBook(book) {
    if (selectedBooks.find(b => b.id === book.id)) return;
    selectedBooks.push(book);
    renderSelected();
  }

  function removeSelectedBook(id) {
    selectedBooks = selectedBooks.filter(b => b.id !== id);
    renderSelected();
  }

  function renderSelected() {
    selectedContainer.innerHTML = '';
    if (!selectedBooks.length) {
      selectedContainer.innerHTML = '<p><em>No hay libros seleccionados.</em></p>';
      return;
    }
    const list = document.createElement('ul');
    selectedBooks.forEach(b => {
      const li = document.createElement('li');
      li.textContent = `${b.title} — ${b.author}`;
      list.appendChild(li);
    });
    selectedContainer.appendChild(list);
  }

  // Abrir modal
  agregarBtn.addEventListener('click', () => {
    ensureSearchElements();
    selectedBooks = [];
    renderSelected();
    searchInput.value = '';
    resultsContainer.innerHTML = '';
    modal.style.display = 'flex';
  });

  // Cerrar modal
  cerrar.addEventListener('click', () => modal.style.display = 'none');
  window.addEventListener('click', (e) => { if (e.target === modal) modal.style.display = 'none'; });

  // Enviar formulario: crear lista y renderizarla en la página
  form.addEventListener('submit', (e) => {
    e.preventDefault();
    const nombre = document.getElementById('nombre-lista').value.trim();
    const descripcion = document.getElementById('descripcion-lista').value.trim();

    if (!nombre) {
      alert('Debes indicar un nombre para la lista.');
      return;
    }

    const card = document.createElement('div');
    card.className = 'lista-card';
    card.style.border = '1px solid #ddd';
    card.style.padding = '10px';
    card.style.margin = '10px 0';

    const h3 = document.createElement('h3');
    h3.textContent = nombre;

    const p = document.createElement('p');
    p.textContent = descripcion;

    const ul = document.createElement('ul');
    if (selectedBooks.length === 0) {
      const li = document.createElement('li');
      li.textContent = 'No hay libros en esta lista.';
      ul.appendChild(li);
    } else {
      selectedBooks.forEach(b => {
        const li = document.createElement('li');
        li.textContent = `${b.title} — ${b.author}`;
        ul.appendChild(li);
      });
    }

    card.appendChild(h3);
    card.appendChild(p);
    card.appendChild(ul);

    listasContainer.appendChild(card);

    // Cerrar y limpiar
    modal.style.display = 'none';
    form.reset();
    selectedBooks = [];
  });

});
