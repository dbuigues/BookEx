package es.rafapuig.pmdm.compose.proyecto.di

import es.rafapuig.pmdm.compose.proyecto.data.repository.ListsRemoteRepositoryImpl
import es.rafapuig.pmdm.compose.proyecto.domain.repository.ListsRepository
import org.koin.dsl.module

val listsModule = module {
    single<ListsRepository> { ListsRemoteRepositoryImpl(get(), get(), get()) }
}

