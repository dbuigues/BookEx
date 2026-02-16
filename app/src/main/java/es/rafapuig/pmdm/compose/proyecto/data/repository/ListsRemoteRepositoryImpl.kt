package es.rafapuig.pmdm.compose.proyecto.data.repository

import es.rafapuig.pmdm.compose.proyecto.data.remote.ApiResponse
import es.rafapuig.pmdm.compose.proyecto.data.remote.safeApiCall
import es.rafapuig.pmdm.compose.proyecto.data.remote.api.ListaApiService
import es.rafapuig.pmdm.compose.proyecto.data.remote.api.LibroListaApiService
import es.rafapuig.pmdm.compose.proyecto.data.remote.dto.ListaDto
import es.rafapuig.pmdm.compose.proyecto.data.remote.dto.LibroListaDto
import es.rafapuig.pmdm.compose.proyecto.domain.model.BookList

class ListsRemoteRepositoryImpl(
    private val listaApiService: ListaApiService,
    private val libroListaApiService: LibroListaApiService
) : es.rafapuig.pmdm.compose.proyecto.domain.repository.ListsRepository {

    private fun dtoToDomain(dto: ListaDto): BookList {
        return BookList(
            id = dto.idLista?.toString() ?: "",
            name = dto.nombreLista,
            ownerId = dto.idUsuario,
            createdAt = "",
            items = mutableListOf()
        )
    }

    override suspend fun getListsForOwner(ownerId: Long): List<BookList> {
        return when (val resp = safeApiCall { listaApiService.getListasByUsuarioId(ownerId) }) {
            is ApiResponse.Success -> resp.data.map { dtoToDomain(it) }
            is ApiResponse.Error -> throw Exception(resp.message)
            is ApiResponse.Loading -> emptyList()
        }
    }

    override suspend fun createList(ownerId: Long, name: String): BookList {
        val payload = ListaDto(idLista = null, idUsuario = ownerId, nombreLista = name)
        return when (val resp = safeApiCall { listaApiService.createLista(payload) }) {
            is ApiResponse.Success -> dtoToDomain(resp.data)
            is ApiResponse.Error -> throw Exception(resp.message)
            is ApiResponse.Loading -> throw Exception("Loading")
        }
    }

    override suspend fun deleteList(ownerId: Long, listId: String): Boolean {
        val idLong = try { listId.toLong() } catch (_: Exception) { return false }
        return when (safeApiCall { listaApiService.deleteLista(idLong) }) {
            is ApiResponse.Success -> true
            is ApiResponse.Error -> false
            is ApiResponse.Loading -> false
        }
    }

    override suspend fun addBookToList(ownerId: Long, listId: String, bookId: String): Boolean {
        val idLong = try { listId.toLong() } catch (_: Exception) { return false }
        val payload = LibroListaDto(id = null, idLista = idLong, googleBookId = bookId)
        return when (safeApiCall { libroListaApiService.createLibroLista(payload) }) {
            is ApiResponse.Success -> true
            is ApiResponse.Error -> false
            is ApiResponse.Loading -> false
        }
    }
}
