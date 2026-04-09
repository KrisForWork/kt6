package com.example.kt6_2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.kt6_2.di.AppModule
import com.example.kt6_2.navigation.NavGraph
import com.example.kt6_2.presentation.list.LaureatesListViewModel
import com.example.kt6_2.ui.theme.Kt6_2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Kt6_2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    val listViewModel: LaureatesListViewModel = viewModel(
                        factory = AppModule.provideLaureatesListViewModelFactory()
                    )

                    NavGraph(
                        navController = navController,
                        listViewModel = listViewModel
                    )
                }
            }
        }
    }
}