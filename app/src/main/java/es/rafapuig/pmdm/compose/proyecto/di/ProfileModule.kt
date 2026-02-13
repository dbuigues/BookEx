package es.rafapuig.pmdm.compose.proyecto.di

import es.rafapuig.pmdm.compose.proyecto.feature.profile.presentation.ProfileViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val profileModule = module {
    viewModel { ProfileViewModel(get()) }
}
