package es.rafapuig.pmdm.compose.proyecto.di

import es.rafapuig.pmdm.compose.proyecto.domain.usecase.home.GetPopularBooksUseCase
import es.rafapuig.pmdm.compose.proyecto.presentation.home.HomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Módulo de Koin para la feature Home
 * Proporciona las dependencias necesarias para la pantalla de inicio
 */
val homeModule = module {


    // Use Cases
    factory { GetPopularBooksUseCase(get()) }

    // ViewModels
    viewModel { HomeViewModel(get(), get()) }
}
