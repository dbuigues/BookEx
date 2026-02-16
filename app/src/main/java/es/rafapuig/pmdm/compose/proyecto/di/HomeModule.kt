package es.rafapuig.pmdm.compose.proyecto.di

import es.rafapuig.pmdm.compose.proyecto.data.repository.BooksRepositoryImpl
import es.rafapuig.pmdm.compose.proyecto.domain.repository.BooksRepository
import es.rafapuig.pmdm.compose.proyecto.feature.home.domain.GetPopularBooksUseCase
import es.rafapuig.pmdm.compose.proyecto.feature.home.presentation.HomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Módulo de Koin para la feature Home
 * Proporciona las dependencias necesarias para la pantalla de inicio
 */
val homeModule = module {

    // Repository
    single<BooksRepository> { BooksRepositoryImpl(get()) }

    // Use Cases
    factory { GetPopularBooksUseCase(get()) }

    // ViewModels
    viewModel { HomeViewModel(get(), get()) }
}
