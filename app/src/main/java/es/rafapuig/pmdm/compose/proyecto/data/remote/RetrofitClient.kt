package es.rafapuig.pmdm.compose.proyecto.data.remote

import android.content.Context
import com.google.gson.GsonBuilder
import okhttp3.Cache
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "https://bookex-u97b.onrender.com/"
    private const val CACHE_SIZE = 10L * 1024 * 1024 // 10 MB

    private var cache: Cache? = null

    fun init(context: Context) {
        cache = Cache(File(context.cacheDir, "http_cache"), CACHE_SIZE)
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .apply { cache?.let { cache(it) } }
            .connectionPool(ConnectionPool(5, 30, TimeUnit.SECONDS))
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    private val gson = GsonBuilder()
        .setLenient()
        .create()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }

    /**
     * Envía una petición HEAD ligera al servidor para despertarlo del cold-start
     * y precalentar el connection pool del OkHttpClient compartido.
     */
    fun warmUp() {
        try {
            val request = okhttp3.Request.Builder()
                .url(BASE_URL + "api/listas")
                .head()
                .build()
            okHttpClient.newCall(request).execute().close()
        } catch (_: Exception) { }
    }
}
