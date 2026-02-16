package es.rafapuig.pmdm.compose.proyecto.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import es.rafapuig.pmdm.compose.proyecto.domain.model.BookList
import es.rafapuig.pmdm.compose.proyecto.domain.model.ListBook
import es.rafapuig.pmdm.compose.proyecto.domain.repository.ListsRepository
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ListsRepositoryImpl(private val context: Context) : ListsRepository {

    private val prefs = context.getSharedPreferences("bookex_lists", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val DATE_FMT = "yyyy-MM-dd'T'HH:mm:ss'Z'"

    private fun readAll(): MutableList<BookList> {
        val raw = prefs.getString("listas_json", null) ?: return ArrayList()
        return try {
            val type = object : TypeToken<MutableList<BookList>>() {}.type
            gson.fromJson(raw, type) ?: ArrayList()
        } catch (e: Exception) {
            ArrayList()
        }
    }

    private fun writeAll(list: MutableList<BookList>) {
        val raw = gson.toJson(list)
        prefs.edit().putString("listas_json", raw).apply()
    }

    override suspend fun getListsForOwner(ownerId: Long): List<BookList> {
        return readAll().filter { it.ownerId == ownerId }
    }

    override suspend fun createList(ownerId: Long, name: String): BookList {
        val all = readAll()
        // check duplicate case-insensitive
        if (all.any { it.ownerId == ownerId && it.name.equals(name, ignoreCase = true) }) {
            throw IllegalArgumentException("Lista ya existe")
        }
        val id = UUID.randomUUID().toString()
        val now = SimpleDateFormat(DATE_FMT, Locale.US).format(Date())
        val nueva = BookList(id = id, name = name.trim(), ownerId = ownerId, createdAt = now, items = mutableListOf())
        all.add(nueva)
        writeAll(all)
        return nueva
    }

    override suspend fun deleteList(ownerId: Long, listId: String): Boolean {
        val all = readAll()
        val idx = all.indexOfFirst { it.ownerId == ownerId && it.id == listId }
        if (idx == -1) return false
        all.removeAt(idx)
        writeAll(all)
        return true
    }

    override suspend fun addBookToList(ownerId: Long, listId: String, bookId: String): Boolean {
        val all = readAll()
        val lista = all.find { it.ownerId == ownerId && it.id == listId } ?: return false
        if (lista.items.contains(bookId)) return false
        lista.items.add(bookId)
        writeAll(all)
        return true
    }

    override suspend fun getBooksInList(listId: String): List<ListBook> {
        // Esta implementación local solo almacena IDs de libros, no los detalles completos
        // Devuelve una lista con datos básicos
        val all = readAll()
        val lista = all.find { it.id == listId } ?: return emptyList()
        return lista.items.mapIndexed { index, googleBookId ->
            ListBook(
                id = index.toLong(),
                googleBookId = googleBookId,
                title = googleBookId,
                author = "Autor desconocido",
                coverUrl = null,
                review = null,
                rating = null
            )
        }
    }

    override suspend fun removeBookFromList(bookListId: Long): Boolean {
        // En esta implementación local, bookListId es el índice del libro en la lista
        // Esta función no es muy útil sin saber a qué lista pertenece
        // Para simplificar, devolvemos false ya que esta implementación local no soporta esta operación
        return false
    }

    override suspend fun addBookWithReview(
        ownerId: Long,
        listId: String,
        bookId: String,
        review: String,
        rating: Int
    ): Boolean {
        // Esta implementación local no soporta reseñas completas
        return addBookToList(ownerId, listId, bookId)
    }

    override suspend fun getReviewsListId(ownerId: Long): String? {
        val all = readAll()
        return all.find { it.ownerId == ownerId && it.name.equals("Reviews", ignoreCase = true) }?.id
    }
}

