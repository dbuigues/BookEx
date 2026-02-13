# 📡 Conexión con la API de BookEx

## ✅ Configuración Completada

Se ha configurado exitosamente la conexión con la API REST de BookEx usando Retrofit.

### 🔧 Dependencias Añadidas

En `build.gradle.kts`:
- Retrofit 2.9.0
- Gson Converter 2.9.0
- OkHttp 4.12.0
- Logging Interceptor 4.12.0
- Gson 2.10.1

### 📁 Estructura Creada

```
data/remote/
├── api/
│   ├── AuthApiService.kt         # Endpoints de autenticación
│   ├── BookApiService.kt         # Endpoints de libros
│   ├── ReviewApiService.kt       # Endpoints de reseñas
│   └── BookListApiService.kt     # Endpoints de listas
├── dto/
│   ├── UserDto.kt                # Modelos de Usuario
│   ├── BookDto.kt                # Modelos de Libro
│   ├── ReviewDto.kt              # Modelos de Reseña
│   └── BookListDto.kt            # Modelos de Lista
├── repository/
│   ├── AuthRemoteRepository.kt
│   ├── BookRemoteRepository.kt
│   ├── ReviewRemoteRepository.kt
│   └── BookListRemoteRepository.kt
├── ApiResponse.kt                # Wrapper para respuestas
└── RetrofitClient.kt             # Configuración de Retrofit
```

### 🌐 URL de la API

```kotlin
BASE_URL = "https://bookex-u97b.onrender.com/"
```

### 🔑 Uso Básico

#### 1. Autenticación

```kotlin
// En tu ViewModel o UseCase
class LoginViewModel(
    private val authRepository: AuthRemoteRepository
) : ViewModel() {
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            when (val response = authRepository.login(email, password)) {
                is ApiResponse.Success -> {
                    val token = response.data.token
                    val user = response.data.user
                    // Guardar token y usuario
                }
                is ApiResponse.Error -> {
                    // Manejar error
                    Log.e("Login", response.message)
                }
                is ApiResponse.Loading -> {
                    // Mostrar loading
                }
            }
        }
    }
}
```

#### 2. Obtener Libros

```kotlin
class BooksViewModel(
    private val bookRepository: BookRemoteRepository
) : ViewModel() {
    
    fun getAllBooks() {
        viewModelScope.launch {
            when (val response = bookRepository.getAllBooks()) {
                is ApiResponse.Success -> {
                    val books = response.data
                    // Actualizar UI
                }
                is ApiResponse.Error -> {
                    // Manejar error
                }
                is ApiResponse.Loading -> {
                    // Mostrar loading
                }
            }
        }
    }
    
    fun searchBooks(query: String) {
        viewModelScope.launch {
            val response = bookRepository.searchBooks(query)
            // Manejar respuesta
        }
    }
}
```

#### 3. Crear Reseña

```kotlin
class ReviewViewModel(
    private val reviewRepository: ReviewRemoteRepository
) : ViewModel() {
    
    fun createReview(token: String, bookId: Long, rating: Int, comment: String) {
        viewModelScope.launch {
            when (val response = reviewRepository.createReview(token, bookId, rating, comment)) {
                is ApiResponse.Success -> {
                    val review = response.data
                    // Reseña creada exitosamente
                }
                is ApiResponse.Error -> {
                    // Manejar error
                }
                is ApiResponse.Loading -> {
                    // Mostrar loading
                }
            }
        }
    }
}
```

#### 4. Gestionar Listas

```kotlin
class ListsViewModel(
    private val listRepository: BookListRemoteRepository
) : ViewModel() {
    
    fun createList(token: String, name: String, description: String?, isPublic: Boolean) {
        viewModelScope.launch {
            val response = listRepository.createList(token, name, description, isPublic)
            // Manejar respuesta
        }
    }
    
    fun addBookToList(token: String, listId: Long, bookId: Long) {
        viewModelScope.launch {
            val response = listRepository.addBookToList(token, listId, bookId)
            // Manejar respuesta
        }
    }
}
```

### 🔐 Autenticación con Token

Todos los endpoints protegidos requieren el token en el header:

```kotlin
Authorization: Bearer {token}
```

Los repositorios ya manejan esto automáticamente:

```kotlin
// Internamente, el repositorio añade el prefijo "Bearer "
authRepository.getCurrentUser(token)
bookRepository.createBook(token, bookRequest)
```

### 📦 Inyección de Dependencias

Los servicios y repositorios están configurados en Koin (`NetworkModule.kt`):

```kotlin
val networkModule = module {
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
```

Para usar en ViewModels:

```kotlin
class MyViewModel(
    private val bookRepository: BookRemoteRepository  // Se inyecta automáticamente
) : ViewModel() {
    // ...
}
```

### ⚙️ Configuración de Retrofit

El `RetrofitClient` está configurado con:
- **Logging**: Imprime todas las requests/responses en logcat
- **Timeouts**: 30 segundos para conexión, lectura y escritura
- **Gson**: Para serialización/deserialización JSON
- **OkHttp**: Cliente HTTP con logging interceptor

### 🔄 ApiResponse Wrapper

Todas las llamadas devuelven `ApiResponse<T>`:

```kotlin
sealed class ApiResponse<out T> {
    data class Success<out T>(val data: T) : ApiResponse<T>()
    data class Error(val message: String, val code: Int? = null) : ApiResponse<Nothing>()
    object Loading : ApiResponse<Nothing>()
}
```

Esto permite manejar estados de manera consistente en toda la app.

### 🌍 Permisos Añadidos

En `AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

### 📚 Endpoints Disponibles

#### Auth
- `POST /api/auth/register` - Registrar usuario
- `POST /api/auth/login` - Iniciar sesión
- `GET /api/auth/me` - Obtener usuario actual
- `PUT /api/auth/profile` - Actualizar perfil

#### Books
- `GET /api/books` - Obtener todos los libros
- `GET /api/books/{id}` - Obtener libro por ID
- `GET /api/books/search?query={query}` - Buscar libros
- `GET /api/books/author/{author}` - Libros por autor
- `GET /api/books/genre/{genre}` - Libros por género
- `POST /api/books` - Crear libro (requiere auth)
- `PUT /api/books/{id}` - Actualizar libro (requiere auth)
- `DELETE /api/books/{id}` - Eliminar libro (requiere auth)

#### Reviews
- `GET /api/reviews/book/{bookId}` - Reseñas de un libro
- `GET /api/reviews/user/{userId}` - Reseñas de un usuario
- `POST /api/reviews` - Crear reseña (requiere auth)
- `PUT /api/reviews/{id}` - Actualizar reseña (requiere auth)
- `DELETE /api/reviews/{id}` - Eliminar reseña (requiere auth)

#### Lists
- `GET /api/lists/user/{userId}` - Listas de un usuario
- `GET /api/lists/{id}` - Obtener lista por ID
- `GET /api/lists/public` - Listas públicas
- `POST /api/lists` - Crear lista (requiere auth)
- `PUT /api/lists/{id}` - Actualizar lista (requiere auth)
- `DELETE /api/lists/{id}` - Eliminar lista (requiere auth)
- `POST /api/lists/{listId}/books` - Añadir libro a lista
- `DELETE /api/lists/{listId}/books/{bookId}` - Quitar libro de lista

### 🚀 Próximos Pasos

1. Integrar los repositorios en tus UseCases existentes
2. Actualizar los ViewModels para usar las llamadas reales
3. Implementar almacenamiento local del token (SharedPreferences/DataStore)
4. Añadir manejo de errores más específico
5. Implementar cache de datos si es necesario
6. Añadir refresh token si la API lo soporta

---

**¡La infraestructura de red está lista para ser usada! 🎉**
