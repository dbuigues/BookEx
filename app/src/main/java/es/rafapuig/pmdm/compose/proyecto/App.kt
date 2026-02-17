package es.rafapuig.pmdm.compose.proyecto

import android.app.Application
import es.rafapuig.pmdm.compose.proyecto.di.authModule
import es.rafapuig.pmdm.compose.proyecto.di.booksModule
import es.rafapuig.pmdm.compose.proyecto.di.homeModule
import es.rafapuig.pmdm.compose.proyecto.di.listsModule
import es.rafapuig.pmdm.compose.proyecto.di.navigationModule
import es.rafapuig.pmdm.compose.proyecto.di.networkModule
import es.rafapuig.pmdm.compose.proyecto.di.profileModule
import es.rafapuig.pmdm.compose.proyecto.di.reviewsModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(networkModule, listsModule, authModule, profileModule, homeModule, booksModule, reviewsModule, navigationModule)
        }
    }
}