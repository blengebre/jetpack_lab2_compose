package com.example.labassignment2.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.labassignment2.model.Todo
import com.example.labassignment2.repository.TodoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TodoDetailViewModel(
    private val repository: TodoRepository,
    private val todoId: Int
) : ViewModel() {
    private val _uiState = MutableStateFlow<TodoDetailUiState>(TodoDetailUiState.Loading)
    val uiState: StateFlow<TodoDetailUiState> = _uiState.asStateFlow()

    init {
        loadTodo()
    }

    private fun loadTodo() {
        viewModelScope.launch {
            try {
                val todo = repository.getTodoById(todoId)
                _uiState.value = if (todo != null) {
                    TodoDetailUiState.Success(todo)
                } else {
                    TodoDetailUiState.Error("Todo not found")
                }
            } catch (e: Exception) {
                _uiState.value = TodoDetailUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

class TodoDetailViewModelFactory(
    private val repository: TodoRepository,
    private val todoId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TodoDetailViewModel(repository, todoId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

sealed class TodoDetailUiState {
    data object Loading : TodoDetailUiState()
    data class Success(val todo: Todo) : TodoDetailUiState()
    data class Error(val message: String) : TodoDetailUiState()
}