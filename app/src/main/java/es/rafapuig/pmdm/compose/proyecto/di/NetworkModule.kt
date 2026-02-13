package es.rafapuig.pmdm.compose.proyecto.di

import es.rafapuig.pmdm.compose.proyecto.data.local.TokenManager
import es.rafapuig.pmdm.compose.proyecto.data.remote.RetrofitClient
import es.rafapuig.pmdm.compose.proyecto.data.remote.api.AuthApiService
import es.rafapuig.pmdm.compose.proyecto.data.remote.api.BookApiService
import es.rafapuig.pmdm.compose.proyecto.data.remote.api.BookListApiService
import es.rafapuig.pmdm.compose.proyecto.data.remote.api.ReviewApiService
import es.rafapuig.pmdm.compose.proyecto.data.remote.repository.AuthRemoteRepository
import es.rafapuig.pmdm.compose.proyecto.data.remote.repository.BookListRemoteRepository
import es.rafapuig.pmdm.compose.proyecto.data.remote.repository.BookRemoteRepository
import es.rafapuig.pmdm.compose.proyecto.data.remote.repository.ReviewRemoteRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val networkModule = module {

    // Token Manager
    single { TokenManager(androidContext()) }

    // API Services
    single { RetrofitClient.createService(AuthApiService::class.java) }
    single { RetrofitClient.createService(BookApiService::class.java) }
    single { RetrofitClient.createService(ReviewApiService::class.java) }
    single { RetrofitClient.createService(BookListApiService::class.java) }

    // Remote Repositories
    single { AuthRemoteRepository(get()) }
    single { BookRemoteRepository(get()) }
    single { ReviewRemoteRepository(get()) }
    single { BookListRemoteRepository(get()) }
}
