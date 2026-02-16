package es.rafapuig.pmdm.compose.proyecto.domain.repository

import es.rafapuig.pmdm.compose.proyecto.domain.model.BookList

interface ListsRepository {
    suspend fun getListsForOwner(ownerId: Long): List<BookList>
    suspend fun createList(ownerId: Long, name: String): BookList
    suspend fun deleteList(ownerId: Long, listId: String): Boolean
    suspend fun addBookToList(ownerId: Long, listId: String, bookId: String): Boolean
}

