package com.example.labassignment2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.labassignment2.data.TodoDatabase
import com.example.labassignment2.network.TodoApiService
import com.example.labassignment2.repository.TodoRepository
import com.example.labassignment2.ui.detail.TodoDetailScreen
import com.example.labassignment2.ui.list.TodoListScreen
import com.example.labassignment2.ui.list.TodoListViewModel
import com.example.labassignment2.ui.list.TodoListViewModelFactory
import com.example.labassignment2.ui.theme.TodoAppTheme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService = retrofit.create(TodoApiService::class.java)

        // Initialize Room
        val todoDao = TodoDatabase.getDatabase(applicationContext).todoDao()

        // Initialize Repository
        val repository = TodoRepository(todoDao, apiService)

        setContent {
            TodoAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "todo_list"
                    ) {
                        composable("todo_list") {
                            val viewModel: TodoListViewModel = viewModel(
                                factory = TodoListViewModelFactory(repository)
                            )
                            TodoListScreen(
                                onTodoClick = { todoId ->
                                    navController.navigate("todo_detail/$todoId")
                                },
                                viewModel = viewModel
                            )
                        }
                        composable("todo_detail/{todoId}") { backStackEntry ->
                            val todoId = backStackEntry.arguments?.getString("todoId")?.toIntOrNull()
                            if (todoId != null) {
                                TodoDetailScreen(
                                    todoId = todoId,
                                    onBackClick = { navController.popBackStack() },
                                    repository = repository
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}