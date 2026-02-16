package es.rafapuig.pmdm.compose.proyecto.di

import es.rafapuig.pmdm.compose.proyecto.domain.usecase.books.SearchBooksUseCase
import es.rafapuig.pmdm.compose.proyecto.presentation.books.BooksViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val booksModule = module {
    factory { SearchBooksUseCase(get()) }
    viewModel { BooksViewModel(get()) }
}

