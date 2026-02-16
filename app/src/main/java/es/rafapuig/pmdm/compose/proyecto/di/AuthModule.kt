package es.rafapuig.pmdm.compose.proyecto.di

import es.rafapuig.pmdm.compose.proyecto.data.repository.AuthRepositoryImpl
import es.rafapuig.pmdm.compose.proyecto.domain.repository.AuthRepository
import es.rafapuig.pmdm.compose.proyecto.domain.usecase.auth.LoginUseCase
import es.rafapuig.pmdm.compose.proyecto.domain.usecase.auth.RegisterUseCase
import es.rafapuig.pmdm.compose.proyecto.presentation.auth.login.LoginViewModel
import es.rafapuig.pmdm.compose.proyecto.presentation.auth.register.RegisterViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val authModule = module {

    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }

    factory { LoginUseCase(get()) }
    factory { RegisterUseCase(get()) }

    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel(get()) }

}