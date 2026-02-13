package es.rafapuig.pmdm.compose.proyecto

import android.app.Application
import es.rafapuig.pmdm.compose.proyecto.di.authModule
import es.rafapuig.pmdm.compose.proyecto.di.networkModule
import es.rafapuig.pmdm.compose.proyecto.di.profileModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(networkModule, authModule, profileModule)
        }
    }
}