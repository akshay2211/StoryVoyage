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

class HomeViewModel : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val _events = Channel<HomeEvent>()
    val events = _events.receiveAsFlow()

    init {
        processIntent(HomeIntent.LoadBooks)
    }

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

    private fun navigateToBook(bookId: String) {
        viewModelScope.launch {
            _events.send(HomeEvent.NavigateToRoute("detail/$bookId"))
        }
    }

    private fun navigateToSettings(route: String) {
        viewModelScope.launch {
            _events.send(HomeEvent.NavigateToRoute(route))
        }
    }

    private fun setCurrentPage(index: Int) {
        _state.update { it.copy(currentPageIndex = index) }
    }

    private fun toggleDrawer() {
        _state.update { it.copy(isDrawerOpen = !it.isDrawerOpen) }
    }

    private fun setDrawerOpen(isOpen: Boolean) {
        _state.update { it.copy(isDrawerOpen = isOpen) }
    }
}