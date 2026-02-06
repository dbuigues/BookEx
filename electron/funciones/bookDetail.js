// bookDetail.js
// Este script carga la información del libro seleccionado desde la API de Google Books

const API_KEY = "AIzaSyAA8hrOze05X9GGh9KbKBuRd4Lt_9zCAt0";

function getQueryParam(param) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(param);
}

function stripHtml(html) {
  const div = document.createElement("div");
  div.innerHTML = html;
  return div.textContent || div.innerText || "";
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
                year: book.publishedDate || 'Año desconocido',
                description: stripHtml(book.description) || 'Descripción no disponible',
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
    const id = getQueryParam('id');
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
    document.getElementById('bookYear').textContent = book.year;
    document.getElementById('bookDescription').textContent = book.description;
    document.getElementById('bookCover').src = book.cover;
}

document.addEventListener('DOMContentLoaded', loadBookDetail);