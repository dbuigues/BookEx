// bookDetail.js
// Este script carga la información del libro seleccionado desde la API de Google Books

function getQueryParam(param) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(param);
}

async function fetchBookData(isbn) {
    try {
        const response = await fetch(`https://www.googleapis.com/books/v1/volumes?q=isbn:${isbn}`);
        const data = await response.json();
        if (data.items && data.items.length > 0) {
            const book = data.items[0].volumeInfo;
            return {
                title: book.title || 'Título no disponible',
                author: book.authors ? book.authors.join(', ') : 'Autor desconocido',
                genre: book.categories ? book.categories.join(', ') : 'Género desconocido',
                year: book.publishedDate || 'Año desconocido',
                description: book.description || 'Descripción no disponible',
                cover: book.imageLinks?.thumbnail || '../assets/imagenes/default-book.png',
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
    const isbn = getQueryParam('isbn');
    if (!isbn) {
        document.getElementById('bookTitle').textContent = 'Libro no encontrado';
        return;
    }
    const book = await fetchBookData(isbn);
    if (!book) {
        document.getElementById('bookTitle').textContent = 'No se encontraron datos para este libro';
        return;
    }
    document.getElementById('bookTitle').textContent = book.title;
    document.getElementById('bookAuthor').textContent = book.author;
    document.getElementById('bookGenre').textContent = book.genre;
    document.getElementById('bookYear').textContent = book.year;
    document.getElementById('bookDescription').textContent = book.description;
    document.getElementById('bookCover').src = book.cover;
}

document.addEventListener('DOMContentLoaded', loadBookDetail);