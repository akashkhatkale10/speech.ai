package com.speechai.speechai.screens.home

import com.speechai.speechai.R

object HomeUtils {

    val menuItems = listOf(
        MenuItem(
            "analyse",
            icon = R.drawable.analyse,
            route = HomeScreenRoute.Analyse
        ),
        MenuItem(
            "practice",
            icon = R.drawable.practice,
            route = HomeScreenRoute.Practice
        ),
        MenuItem(
            "history",
            icon = R.drawable.history,
            route = HomeScreenRoute.History
        ),
        MenuItem(
            "progress",
            icon = R.drawable.progress,
            route = HomeScreenRoute.Progress
        )
    )

}