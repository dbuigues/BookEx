// Función para obtener datos del libro desde Google Books API
// Ahora devuelve un array de coincidencias
let searchBooksByName = async (nombre) => {
    console.log("Cargando libros...");

  try {
    const response = await fetch(`https://www.googleapis.com/books/v1/volumes?q=${encodeURIComponent(nombre)}`);
    const data = await response.json();
    if (data.items && data.items.length > 0) {
      return data.items.map(item => {
        const book = item.volumeInfo;
        let thumbnail = '../assets/imagenes/logo.png';
        if (book.imageLinks && book.imageLinks.thumbnail) {
          thumbnail = book.imageLinks.thumbnail;
        }
        return {
          isbn: book.industryIdentifiers ? book.industryIdentifiers[0].identifier : 'N/A',
          title: book.title || 'Título no disponible',
          author: book.authors ? book.authors.join(', ') : 'Autor desconocido',
          description: book.description || 'Descripción no disponible',
          thumbnail: thumbnail,
          link: book.previewLink || '#'
        };
      });
    }
    return [];
  } catch (error) {
    console.error(`Error al buscar libros por nombre ${nombre}:`, error);
    return [];
  }
}

// Función para crear una tarjeta de libro
let createBookCard = (bookData) => {
  const card = document.createElement('div');
  card.className = 'book-card';
  
  const description = bookData.description.length > 100 
    ? bookData.description.substring(0, 100) + '...' 
    : bookData.description;

    // let newThumbnail = bookData.thumbnail.replace("&source","?fife=w400-h600&source");

  card.innerHTML = `
    <img src="${bookData.thumbnail}" alt="${bookData.title}" class="book-cover" onerror="this.src='../assets/imagenes/placeholder.png'">
    <div class="book-info">
      <h3 class="book-title">${bookData.title}</h3>
      <p class="book-author">por ${bookData.author}</p>
      <p class="book-description">${description}</p>
      <p class="book-isbn">ISBN: ${bookData.isbn}</p>
    </div>
  `;
  
  card.addEventListener('click', () => {
    window.open(`BookDetail.html?isbn=${bookData.isbn}`, '_blank');
  });
  
  return card;
}

// Función para cargar todos los libros
let loadBooks = async () => {
  const container = document.getElementById('booksContainer');
  
  if (!container) {
    console.error('Contenedor de libros no encontrado');
    return;
  }
  
  // Agregar spinner de carga
  container.innerHTML = '<img width="30px" style="grid-column:1; margin: auto; margin-right: 0px" src="../assets/loading.gif"><p style="grid-column: 2/-1; padding: 40px; padding-left:20px">Cargando libros...</p>';
  const books = [];
  
  // Obtener datos de todos los libros
  for (const isbn of isbns) {
    const bookData = await getBookData(isbn);
    if (bookData) {
      books.push(bookData);
      console.log("libro insertado");
    }
    // Pequeño delay para no sobrecargar la API
    await new Promise(resolve => setTimeout(resolve, 100));
  }
  
  // Limpiar contenedor
  container.innerHTML = '';
  // Crear y agregar tarjetas
  books.forEach(book => {
    const card = createBookCard(book);
    container.appendChild(card);
  });
}

let debounceTimeout;
document.getElementById('searchInput').addEventListener('input', (event) => {
  clearTimeout(debounceTimeout);
  debounceTimeout = setTimeout(async () => {
    const query = event.target.value.trim();
    const container = document.getElementById('booksContainer');
    container.innerHTML = '';
    if (query.length === 0) {
      return;
    }
    // Mostrar spinner de carga
    container.innerHTML = '<img width="30px" style="grid-column:1; margin: auto; margin-right: 0px" src="../assets/loading.gif"><p style="grid-column: 2/-1; padding: 40px; padding-left:20px">Buscando libros...</p>';
    const books = await searchBooksByName(query);
    container.innerHTML = '';
    if (books.length === 0) {
      container.innerHTML = '<p style="grid-column: 1/-1; text-align:center;">No se encontraron resultados.</p>';
      return;
    }
    books.forEach(book => {
      const card = createBookCard(book);
      container.appendChild(card);
    });
  }, 400); // 400 ms de espera tras dejar de escribir
});