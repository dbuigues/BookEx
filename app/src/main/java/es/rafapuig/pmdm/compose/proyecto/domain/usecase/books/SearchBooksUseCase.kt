package es.rafapuig.pmdm.compose.proyecto.domain.usecase.books

import es.rafapuig.pmdm.compose.proyecto.domain.model.Book
import es.rafapuig.pmdm.compose.proyecto.domain.repository.BooksRepository

class SearchBooksUseCase(
    private val booksRepository: BooksRepository
) {
    suspend operator fun invoke(query: String): Result<List<Book>> {
        return booksRepository.searchBooks(query = query, maxResults = 20)
    }
}