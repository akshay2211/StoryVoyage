package io.ak1.demo.presentation.home

import io.ak1.demo.domain.model.Book

/**
 * Represents the UI state for the Home screen's book library.
 * 
 * @param isLoading Indicates if books are currently being loaded
 * @param books The complete list of available books
 * @param currentPageIndex Current page index for pagination or view pager
 * @param isDrawerOpen State of the navigation drawer
 * @param searchQuery Current search query string
 * @param filteredBooks Books filtered based on the search query
 * @param error Error message if book loading fails
 */
data class HomeState(
    val isLoading: Boolean = false,
    val books: List<Book> = emptyList(),
    val currentPageIndex: Int = 0,
    val isDrawerOpen: Boolean = false,
    val searchQuery: String = "",
    val filteredBooks: List<Book> = emptyList(),
    val error: String? = null
)

/**
 * Sealed interface representing user intents for the Home screen.
 */
sealed interface HomeIntent {
    data object LoadBooks : HomeIntent
    data class UpdateSearchQuery(val query: String) : HomeIntent
    data class NavigateToBook(val bookId: String) : HomeIntent
    data class NavigateToSettings(val route: String) : HomeIntent
    data class SetCurrentPage(val index: Int) : HomeIntent
    data object ToggleDrawer : HomeIntent
    data class SetDrawerOpen(val isOpen: Boolean) : HomeIntent
}

/**
 * Sealed interface representing one-time events for the Home screen UI.
 */
sealed interface HomeEvent {
    data class NavigateToRoute(val route: String) : HomeEvent
    data class ShowError(val message: String) : HomeEvent
}