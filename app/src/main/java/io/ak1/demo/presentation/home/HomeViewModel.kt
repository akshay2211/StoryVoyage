package io.ak1.demo.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ak1.demo.data.repository.Books
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for managing the Home screen's book library functionality.
 * 
 * This ViewModel implements the MVI (Model-View-Intent) pattern for handling:
 * - Book library loading and display
 * - Search functionality with real-time filtering
 * - Navigation drawer state management
 * - Page navigation within the book grid
 * - Error handling and user events
 * 
 * The Home screen serves as the main entry point for book discovery and selection.
 */
class HomeViewModel : ViewModel() {

    /** Mutable state flow for managing Home screen UI state */
    private val _state = MutableStateFlow(HomeState())
    
    /** Exposed read-only state flow for UI observation */
    val state: StateFlow<HomeState> = _state.asStateFlow()

    /** Channel for one-time UI events */
    private val _events = Channel<HomeEvent>()
    
    /** Exposed flow for UI events like navigation and error messages */
    val events = _events.receiveAsFlow()

    /**
     * Initializes the ViewModel by automatically loading the book library.
     */
    init {
        processIntent(HomeIntent.LoadBooks)
    }

    /**
     * Processes user intents using the MVI pattern.
     * Routes different types of user actions to appropriate handler methods.
     * 
     * @param intent The user intent to process
     */
    fun processIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadBooks -> loadBooks()
            is HomeIntent.UpdateSearchQuery -> updateSearchQuery(intent.query)
            is HomeIntent.NavigateToBook -> navigateToBook(intent.bookId)
            is HomeIntent.NavigateToSettings -> navigateToSettings(intent.route)
            is HomeIntent.SetCurrentPage -> setCurrentPage(intent.index)
            is HomeIntent.ToggleDrawer -> toggleDrawer()
            is HomeIntent.SetDrawerOpen -> setDrawerOpen(intent.isOpen)
        }
    }

    /**
     * Loads the book library from the data source.
     * Updates UI state with loading indicators and handles errors gracefully.
     */
    private fun loadBooks() {
        _state.update { it.copy(isLoading = true, error = null) }
        
        viewModelScope.launch {
            try {
                val books = Books.list
                _state.update { 
                    it.copy(
                        isLoading = false,
                        books = books,
                        filteredBooks = books,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load books"
                    )
                }
                _events.trySend(HomeEvent.ShowError(e.message ?: "Failed to load books"))
            }
        }
    }

    /**
     * Updates the search query and filters books based on the input.
     * Searches across book title, author, and description fields.
     * 
     * @param query The search query string to filter books
     */
    private fun updateSearchQuery(query: String) {
        _state.update { currentState ->
            val filteredBooks = if (query.isBlank()) {
                currentState.books
            } else {
                currentState.books.filter { book ->
                    book.title.contains(query, ignoreCase = true) ||
                    book.author.contains(query, ignoreCase = true) ||
                    book.description.contains(query, ignoreCase = true)
                }
            }
            
            currentState.copy(
                searchQuery = query,
                filteredBooks = filteredBooks
            )
        }
    }

    /**
     * Triggers navigation to a specific book's detail screen.
     * 
     * @param bookId The unique identifier of the book to navigate to
     */
    private fun navigateToBook(bookId: String) {
        viewModelScope.launch {
            _events.send(HomeEvent.NavigateToRoute("detail/$bookId"))
        }
    }

    /**
     * Triggers navigation to a settings or other screen.
     * 
     * @param route The navigation route to navigate to
     */
    private fun navigateToSettings(route: String) {
        viewModelScope.launch {
            _events.send(HomeEvent.NavigateToRoute(route))
        }
    }

    /**
     * Updates the current page index for pagination or view pager.
     * 
     * @param index The new page index to set
     */
    private fun setCurrentPage(index: Int) {
        _state.update { it.copy(currentPageIndex = index) }
    }

    /**
     * Toggles the navigation drawer open/closed state.
     */
    private fun toggleDrawer() {
        _state.update { it.copy(isDrawerOpen = !it.isDrawerOpen) }
    }

    /**
     * Sets the navigation drawer to a specific open/closed state.
     * 
     * @param isOpen True to open the drawer, false to close it
     */
    private fun setDrawerOpen(isOpen: Boolean) {
        _state.update { it.copy(isDrawerOpen = isOpen) }
    }
}