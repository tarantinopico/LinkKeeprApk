package com.tarantino.linkkeeper

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    KeeprNavHost(navController = navController)
}
