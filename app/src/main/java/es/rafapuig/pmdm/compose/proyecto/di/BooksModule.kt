package es.rafapuig.pmdm.compose.proyecto.di

import es.rafapuig.pmdm.compose.proyecto.feature.books.domain.SearchBooksUseCase
import es.rafapuig.pmdm.compose.proyecto.feature.books.presentation.BooksViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val booksModule = module {
    factory { SearchBooksUseCase(get()) }
    viewModel { BooksViewModel(get()) }
}

