package es.rafapuig.pmdm.compose.proyecto.di

import es.rafapuig.pmdm.compose.proyecto.presentation.reviews.ReviewsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val reviewsModule = module {
    viewModel { ReviewsViewModel(get(), get(), get(), get()) }
}

