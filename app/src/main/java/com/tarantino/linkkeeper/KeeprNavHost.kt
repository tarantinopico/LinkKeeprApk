package com.tarantino.linkkeeper

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

@Composable
fun KeeprNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "dashboard") {
        composable("dashboard") {
            DashboardScreen(
                onNavigateToGroupManagement = { navController.navigate("group_management") },
                onNavigateToGroup = { groupId -> navController.navigate("group_links/$groupId") }
            )
        }
        composable(
            route = "group_links/{groupId}",
            arguments = listOf(navArgument("groupId") { type = NavType.LongType })
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getLong("groupId") ?: return@composable
            LinksGridScreen(
                groupId = groupId,
                onBack = { navController.popBackStack() }
            )
        }
        composable("group_management") {
            GroupManagementScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
