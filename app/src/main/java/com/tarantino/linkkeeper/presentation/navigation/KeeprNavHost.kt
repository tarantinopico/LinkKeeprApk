package com.tarantino.linkkeeper

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument

@Composable
fun KeeprNavHost(
    navController: NavHostController, 
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController, 
        startDestination = Screen.Dashboard.route,
        modifier = modifier
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToGroupManagement = { navController.navigate(Screen.GroupManagement.route) },
                onNavigateToGroup = { groupId -> navController.navigate(Screen.GroupLinks.createRoute(groupId)) },
                snackbarHostState = snackbarHostState
            )
        }
        composable(
            route = Screen.GroupLinks.route,
            arguments = listOf(navArgument("groupId") { type = NavType.LongType })
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getLong("groupId") ?: return@composable
            LinksGridScreen(
                groupId = groupId,
                onBack = { navController.popBackStack() },
                snackbarHostState = snackbarHostState
            )
        }
        composable(Screen.GroupManagement.route) {
            GroupManagementScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
