package io.ak1.demo.presentation.home

import io.ak1.demo.domain.model.Book

data class HomeState(
    val isLoading: Boolean = false,
    val books: List<Book> = emptyList(),
    val currentPageIndex: Int = 0,
    val isDrawerOpen: Boolean = false,
    val searchQuery: String = "",
    val filteredBooks: List<Book> = emptyList(),
    val error: String? = null
)

sealed interface HomeIntent {
    data object LoadBooks : HomeIntent
    data class UpdateSearchQuery(val query: String) : HomeIntent
    data class NavigateToBook(val bookId: String) : HomeIntent
    data class NavigateToSettings(val route: String) : HomeIntent
    data class SetCurrentPage(val index: Int) : HomeIntent
    data object ToggleDrawer : HomeIntent
    data class SetDrawerOpen(val isOpen: Boolean) : HomeIntent
}

sealed interface HomeEvent {
    data class NavigateToRoute(val route: String) : HomeEvent
    data class ShowError(val message: String) : HomeEvent
}