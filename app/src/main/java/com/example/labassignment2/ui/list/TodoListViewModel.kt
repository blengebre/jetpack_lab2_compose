package com.example.labassignment2.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.labassignment2.model.Todo
import com.example.labassignment2.repository.TodoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TodoListViewModel(private val repository: TodoRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<TodoListUiState>(TodoListUiState.Loading)
    val uiState: StateFlow<TodoListUiState> = _uiState.asStateFlow()

    init {
        loadTodos()
    }

    fun toggleTodoCompletion(todo: Todo) {
        viewModelScope.launch {
            try {
                val updatedTodo = todo.copy(completed = !todo.completed)
                repository.updateTodo(updatedTodo)
            } catch (e: Exception) {
                _uiState.value = TodoListUiState.Error(e.message ?: "Error updating todo")
            }
        }
    }

    fun reloadTodos() {
        _uiState.value = TodoListUiState.Loading
        loadTodos()
    }

    private fun loadTodos() {
        viewModelScope.launch {
            try {
                repository.refreshTodos()
                repository.todos.collect { todos ->
                    _uiState.value = TodoListUiState.Success(todos)
                }
            } catch (e: Exception) {
                _uiState.value = TodoListUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

class TodoListViewModelFactory(private val repository: TodoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TodoListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

sealed class TodoListUiState {
    data object Loading : TodoListUiState()
    data class Success(val todos: List<Todo>) : TodoListUiState()
    data class Error(val message: String) : TodoListUiState()
}