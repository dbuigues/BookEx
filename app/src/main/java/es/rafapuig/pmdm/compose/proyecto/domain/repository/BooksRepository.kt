package es.rafapuig.pmdm.compose.proyecto.domain.repository

import es.rafapuig.pmdm.compose.proyecto.domain.model.Book

/**
 * Interfaz del repositorio de libros en la capa de dominio
 * Define las operaciones disponibles para obtener datos de libros
 */
interface BooksRepository {
    /**
     * Obtiene los libros populares de la semana
     * @return Result con la lista de libros populares o un error
     */
    suspend fun getPopularBooks(): Result<List<Book>>

    /**
     * Busca libros en la API por un término (query).
     * Nota: el backend actual expone búsqueda, no "listado completo".
     */
    suspend fun searchBooks(
        query: String,
        maxResults: Int = 20
    ): Result<List<Book>>

    /**
     * Busca libros por título
     */
    suspend fun searchBooksByTitle(title: String): Result<List<Book>>
}
