package com.speechai.speechai.screens.home

sealed class HomeScreenRoute(val route: String) {
    object Analyse : HomeScreenRoute("Analyse")
    object Practice : HomeScreenRoute("Practice")
    object History : HomeScreenRoute("History")
    object Progress : HomeScreenRoute("Progress")
}