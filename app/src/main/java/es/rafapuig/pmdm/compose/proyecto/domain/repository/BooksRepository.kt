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
}
