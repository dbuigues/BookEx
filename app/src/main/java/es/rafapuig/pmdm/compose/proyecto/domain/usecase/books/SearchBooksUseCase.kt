package es.rafapuig.pmdm.compose.proyecto.domain.usecase.books

import es.rafapuig.pmdm.compose.proyecto.domain.model.Book
import es.rafapuig.pmdm.compose.proyecto.domain.repository.BooksRepository

class SearchBooksUseCase(
    private val booksRepository: BooksRepository
) {
    suspend operator fun invoke(title: String): Result<List<Book>> {
        return booksRepository.searchBooksByTitle(title = title)
    }
}