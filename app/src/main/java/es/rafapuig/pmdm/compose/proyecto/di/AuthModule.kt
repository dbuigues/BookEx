package es.rafapuig.pmdm.compose.proyecto.di

import es.rafapuig.pmdm.compose.proyecto.feature.auth.data.repository.AuthRepositoryImpl
import es.rafapuig.pmdm.compose.proyecto.feature.auth.domain.repository.AuthRepository
import es.rafapuig.pmdm.compose.proyecto.feature.auth.domain.usecase.LoginUseCase
import es.rafapuig.pmdm.compose.proyecto.feature.auth.presentacion.login.LoginViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val authModule = module {

    single<AuthRepository> { AuthRepositoryImpl() }

    factory { LoginUseCase(get()) }

    viewModel { LoginViewModel(get()) }

}