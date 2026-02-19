package es.rafapuig.pmdm.compose.proyecto

import android.app.Application
import es.rafapuig.pmdm.compose.proyecto.data.remote.RetrofitClient
import es.rafapuig.pmdm.compose.proyecto.di.authModule
import es.rafapuig.pmdm.compose.proyecto.di.booksModule
import es.rafapuig.pmdm.compose.proyecto.di.homeModule
import es.rafapuig.pmdm.compose.proyecto.di.listsModule
import es.rafapuig.pmdm.compose.proyecto.di.navigationModule
import es.rafapuig.pmdm.compose.proyecto.di.networkModule
import es.rafapuig.pmdm.compose.proyecto.di.profileModule
import es.rafapuig.pmdm.compose.proyecto.di.reviewsModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        RetrofitClient.init(this)

        // Warm-up: despertar el servidor de Render reutilizando el OkHttpClient de Retrofit
        // para que el connection pool quede precalentado
        CoroutineScope(Dispatchers.IO).launch {
            try {
                RetrofitClient.warmUp()
            } catch (_: Exception) { }
        }

        startKoin {
            androidContext(this@App)
            modules(networkModule, listsModule, authModule, profileModule, homeModule, booksModule, reviewsModule, navigationModule)
        }
    }
}
