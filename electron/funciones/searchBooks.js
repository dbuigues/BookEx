// Función para obtener datos del libro desde Google Books API
// Ahora devuelve un array de coincidencias
const API_KEY = "AIzaSyAA8hrOze05X9GGh9KbKBuRd4Lt_9zCAt0";

let searchBooksByName = async (nombre) => {

  try {
    const response = await fetch(`https://www.googleapis.com/books/v1/volumes?q=intitle:${encodeURIComponent(nombre)}&key=${API_KEY}`);
    const data = await response.json();
    console.log("Cargando libros...");
    if (data.items && data.items.length > 0) {
      return data.items.map(item => {
        const book = item.volumeInfo;
        let thumbnail = '../assets/imagenes/logo.png';
        if (book.imageLinks && book.imageLinks.thumbnail) {
          thumbnail = book.imageLinks.thumbnail;
        }
        return {
          id: item.id,
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
let searchBooksByISBN = async (isbn) => {
  console.log("Cargando libros...");
  try {
    const response = await fetch(`https://www.googleapis.com/books/v1/volumes?q=isbn:${encodeURIComponent(isbn)}&key=${API_KEY}`);
    const data = await response.json();
    if (data.items && data.items.length > 0) {
      return data.items.map(item => {
        const book = item.volumeInfo;
        let thumbnail = '../assets/imagenes/logo.png';
        if (book.imageLinks && book.imageLinks.thumbnail) {
          thumbnail = book.imageLinks.thumbnail;
        }
        return {
          id: item.id,
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

let searchBooksByAuthor = async (author) => {
  console.log("Cargando libros...");
  try {
    const response = await fetch(`https://www.googleapis.com/books/v1/volumes?q=inauthor:${encodeURIComponent(author)}&key=${API_KEY}`);
    const data = await response.json();
    if (data.items && data.items.length > 0) {
      return data.items.map(item => {
        const book = item.volumeInfo;
        let thumbnail = '../assets/imagenes/logo.png';
        if (book.imageLinks && book.imageLinks.thumbnail) {
          thumbnail = book.imageLinks.thumbnail;
        }
        return {
          id: item.id,
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
    console.error(`Error al buscar libros por autor ${author}:`, error);
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
    <img src="${bookData.thumbnail}" alt="${bookData.title}" class="book-cover" onerror="this.src='../assets/imagenes/logo.png'">
    <div class="book-info">
      <h3 class="book-title">${bookData.title}</h3>
      <p class="book-author">por ${bookData.author}</p>
      <p class="book-description">${description}</p>
      <p class="book-isbn">ISBN: ${bookData.isbn}</p>
    </div>
  `;

  card.addEventListener('click', () => {
    window.open(`BookDetail.html?id=${bookData.id}`, '_blank');
    console.log(bookData.id);
  });

  return card;
}


let debounceTimeout;

document.getElementById('searchNameInput').addEventListener('input', (event) => {
  clearTimeout(debounceTimeout);
  debounceTimeout = setTimeout(async () => {
    const query = event.target.value.trim();
    const container = document.getElementById('booksResultsContainer');
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
  }, 500); // 500 ms de espera tras dejar de escribir
});

document.getElementById('searchAuthorInput').addEventListener('input', (event) => {
  clearTimeout(debounceTimeout);
  debounceTimeout = setTimeout(async () => {
    const query = event.target.value.trim();
    const container = document.getElementById('booksResultsContainer');
    container.innerHTML = '';
    if (query.length === 0) {
      return;
    }
    container.innerHTML = '<img width="30px" style="grid-column:1; margin: auto; margin-right: 0px" src="../assets/loading.gif"><p style="grid-column: 2/-1; padding: 40px; padding-left:20px">Buscando libros...</p>';
    const books = await searchBooksByAuthor(query);
    container.innerHTML = '';
    if (books.length === 0) {
      container.innerHTML = '<p style="grid-column: 1/-1; text-align:center;">No se encontraron resultados.</p>';
      return;
    }
    books.forEach(book => {
      const card = createBookCard(book);
      container.appendChild(card);
    });
  }, 500); // 500 ms de espera tras dejar de escribir
});

document.getElementById('searchISBNInput').addEventListener('input', (event) => {
  clearTimeout(debounceTimeout);
  debounceTimeout = setTimeout(async () => {
    const query = event.target.value.trim();
    const container = document.getElementById('booksResultsContainer');
    container.innerHTML = '';
    if (query.length === 0) {
      return;
    }
    // Mostrar spinner de carga
    container.innerHTML = '<img width="30px" style="grid-column:1; margin: auto; margin-right: 0px" src="../assets/loading.gif"><p style="grid-column: 2/-1; padding: 40px; padding-left:20px">Buscando libros...</p>';
    const books = await searchBooksByISBN(query);
    container.innerHTML = '';
    if (books.length === 0) {
      container.innerHTML = '<p style="grid-column: 1/-1; text-align:center;">No se encontraron resultados.</p>';
      return;
    }
    books.forEach(book => {
      const card = createBookCard(book);
      container.appendChild(card);
    });
  }, 500); // 500 ms de espera tras dejar de escribir
});