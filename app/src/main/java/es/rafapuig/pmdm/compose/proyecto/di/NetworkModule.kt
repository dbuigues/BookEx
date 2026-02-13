package es.rafapuig.pmdm.compose.proyecto.di

import es.rafapuig.pmdm.compose.proyecto.data.local.TokenManager
import es.rafapuig.pmdm.compose.proyecto.data.remote.RetrofitClient
import es.rafapuig.pmdm.compose.proyecto.data.remote.api.AuthApiService
import es.rafapuig.pmdm.compose.proyecto.data.remote.api.BookApiService
import es.rafapuig.pmdm.compose.proyecto.data.remote.api.LibroListaApiService
import es.rafapuig.pmdm.compose.proyecto.data.remote.api.ListaApiService
import es.rafapuig.pmdm.compose.proyecto.data.remote.repository.AuthRemoteRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val networkModule = module {

    // Token Manager
    single { TokenManager(androidContext()) }

    // API Services
    single { RetrofitClient.createService(AuthApiService::class.java) }
    single { RetrofitClient.createService(BookApiService::class.java) }
    single { RetrofitClient.createService(LibroListaApiService::class.java) }
    single { RetrofitClient.createService(ListaApiService::class.java) }

    // Remote Repositories
    single { AuthRemoteRepository(get()) }
}
