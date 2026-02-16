package es.rafapuig.pmdm.compose.proyecto.feature.home.domain

import es.rafapuig.pmdm.compose.proyecto.domain.model.Book
import es.rafapuig.pmdm.compose.proyecto.domain.repository.BooksRepository

/**
 * Caso de uso para obtener los libros populares
 * Encapsula la lógica de negocio para obtener libros populares
 */
class GetPopularBooksUseCase(
    private val booksRepository: BooksRepository
) {
    /**
     * Ejecuta el caso de uso para obtener libros populares
     * @return Result con la lista de libros o un error
     */
    suspend operator fun invoke(): Result<List<Book>> {
        return booksRepository.getPopularBooks()
    }
}
