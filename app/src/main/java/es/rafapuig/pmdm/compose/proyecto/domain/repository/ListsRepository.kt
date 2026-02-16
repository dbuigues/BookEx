package es.rafapuig.pmdm.compose.proyecto.domain.repository

import es.rafapuig.pmdm.compose.proyecto.domain.model.BookList
import es.rafapuig.pmdm.compose.proyecto.domain.model.ListBook

interface ListsRepository {
    suspend fun getListsForOwner(ownerId: Long): List<BookList>
    suspend fun createList(ownerId: Long, name: String): BookList
    suspend fun deleteList(ownerId: Long, listId: String): Boolean
    suspend fun addBookToList(ownerId: Long, listId: String, bookId: String): Boolean
    suspend fun addBookWithReview(
        ownerId: Long,
        listId: String,
        bookId: String,
        review: String,
        rating: Int
    ): Boolean
    suspend fun getBooksInList(listId: String): List<ListBook>
    suspend fun removeBookFromList(bookListId: Long): Boolean
    suspend fun getReviewsListId(ownerId: Long): String?
}

