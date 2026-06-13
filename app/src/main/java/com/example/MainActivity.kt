package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.ui.screens.SmartAssistantAppRouter
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.MainViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Retrieve repository from Application class
        val app = application as SmartAssistantApplication
        val repository = app.repository
        
        // Initialize viewmodel with custom factory
        val viewModel: MainViewModel by viewModels {
            MainViewModelFactory(repository)
        }
        
        enableEdgeToEdge()
        setContent {
            // Observe the dark theme toggle dynamically
            val isDarkThemeEnabled by viewModel.isDarkMode.collectAsState()
            
            MyApplicationTheme(darkTheme = isDarkThemeEnabled) {
                SmartAssistantAppRouter(viewModel = viewModel)
            }
        }
    }
}
