package com.example.labassignment2.repository

import com.example.labassignment2.data.TodoDao
import com.example.labassignment2.model.Todo
import com.example.labassignment2.network.TodoApiService
import kotlinx.coroutines.flow.Flow

class TodoRepository(
    private val todoDao: TodoDao,
    private val apiService: TodoApiService
) {
    val todos: Flow<List<Todo>> = todoDao.getAllTodos()

    suspend fun refreshTodos() {
        try {
            val todos = apiService.getTodos()
            todoDao.insertAll(todos)
        } catch (e: Exception) {
            // Handle error
        }
    }

    suspend fun getTodoById(id: Int): Todo? {
        return todoDao.getTodoById(id) ?: try {
            val todo = apiService.getTodo(id)
            todoDao.insert(todo)
            todo
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateTodo(todo: Todo) {
        try {
            // Update in local database
            todoDao.update(todo)
            
            // Update on the server (if needed)
            apiService.updateTodo(todo.id, todo)
        } catch (e: Exception) {
            // If server update fails, keep local changes
            // You might want to handle this error differently based on your requirements
        }
    }
}