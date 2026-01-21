const isbns = [
  "9780142437230", // Moby-Dick – Herman Melville
  "9780451526342", // Animal Farm – George Orwell
  "9780261103573", // The Lord of the Rings – J.R.R. Tolkien
//   "9780545425117", // The Hunger Games
//   "9780679783268", // Don Quixote – Miguel de Cervantes (modern edition)
//   "9780553386790"  // A Game of Thrones – George R.R. Martin
];

// Función para obtener datos del libro desde Google Books API
async function getBookData(isbn) {
  try {
    const response = await fetch(`https://www.googleapis.com/books/v1/volumes?q=isbn:${isbn}`);
    const data = await response.json();
    
    if (data.items && data.items.length > 0) {
      const book = data.items[0].volumeInfo;
      return {
        isbn: isbn,
        title: book.title || 'Título no disponible',
        author: book.authors ? book.authors.join(', ') : 'Autor desconocido',
        description: book.description || 'Descripción no disponible',
        thumbnail: book.imageLinks?.thumbnail || '../imagenes/placeholder.png',
        link: book.previewLink || '#'
      };
    }
    return null;
  } catch (error) {
    console.error(`Error al obtener datos del ISBN ${isbn}:`, error);
    return null;
  }
}

// Función para crear una tarjeta de libro
function createBookCard(bookData) {
  const card = document.createElement('div');
  card.className = 'book-card';
  
  const description = bookData.description.length > 100 
    ? bookData.description.substring(0, 100) + '...' 
    : bookData.description;

    // let newThumbnail = bookData.thumbnail.replace("&source","?fife=w400-h600&source");

  card.innerHTML = `
    <img src="${bookData.thumbnail}" alt="${bookData.title}" class="book-cover" onerror="this.src='../imagenes/placeholder.png'">
    <div class="book-info">
      <h3 class="book-title">${bookData.title}</h3>
      <p class="book-author">por ${bookData.author}</p>
      <p class="book-description">${description}</p>
      <p class="book-isbn">ISBN: ${bookData.isbn}</p>
    </div>
  `;
  
  card.addEventListener('click', () => {
    window.open(bookData.link, '_blank');
  });
  
  return card;
}

// Función para cargar todos los libros
async function loadBooks() {
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
    await new Promise(resolve => setTimeout(resolve, 150));
  }
  
  // Limpiar contenedor
  container.innerHTML = '';
  
  // Crear y agregar tarjetas
  books.forEach(book => {
    const card = createBookCard(book);
    container.appendChild(card);
  });
}

// Cargar libros cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', loadBooks);

