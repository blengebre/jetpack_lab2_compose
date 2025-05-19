package com.example.labassignment2.network

import com.example.labassignment2.model.Todo
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface TodoApiService {
    @GET("todos")
    suspend fun getTodos(): List<Todo>

    @GET("todos/{id}")
    suspend fun getTodo(@Path("id") id: Int): Todo

    @PUT("todos/{id}")
    suspend fun updateTodo(@Path("id") id: Int, @Body todo: Todo): Todo
}