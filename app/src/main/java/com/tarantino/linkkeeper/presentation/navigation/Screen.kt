package com.tarantino.linkkeeper

sealed class Screen(val route: String) {
    data object Dashboard : Screen("dashboard")
    data object GroupLinks : Screen("group_links/{groupId}") {
        fun createRoute(groupId: Long) = "group_links/$groupId"
    }
    data object GroupManagement : Screen("group_management")
}
