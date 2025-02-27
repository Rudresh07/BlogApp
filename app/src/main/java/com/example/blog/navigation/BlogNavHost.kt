package com.example.blog.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.blog.view.screen.BlogDetail
import com.example.blog.view.screen.BlogDetailScreen
import com.example.blog.view.screen.BlogList
import com.example.blog.view.screen.BlogListScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "BlogListScreen") {
        composable("BlogListScreen") {
            BlogList(navController)
        }
        composable(
            "BlogDetailScreen/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: -1 // Use -1 instead of "Unknown"
            BlogDetail(navController, id)
        }
        composable("SavedCityListScreen") {

        }
    }
}