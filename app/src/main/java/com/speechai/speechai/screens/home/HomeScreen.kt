package com.speechai.speechai.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import com.speechai.speechai.CustomTextStyle
import com.speechai.speechai.R
import com.speechai.speechai.backgroundColor
import com.speechai.speechai.composables.CustomTopBar
import com.speechai.speechai.lightGoldenColor
import com.speechai.speechai.lightYellowColor
import com.speechai.speechai.screens.analyse.AnalyseScreen
import com.speechai.speechai.screens.history.HistoryScreen
import com.speechai.speechai.screens.home.HomeUtils.menuItems
import com.speechai.speechai.screens.practice.PracticeScreen
import com.speechai.speechai.screens.progress.ProgressScreen
import com.speechai.speechai.secondaryColor
import com.speechai.speechai.utils.bounceClick
import com.speechai.speechai.utils.noInteractionClickable
import com.speechai.speechai.whiteColor

@Composable
fun HomeScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var selectedIndex by remember {
        mutableIntStateOf(0)
    }
    val mainNavHost = rememberNavController()

    Scaffold(
        containerColor = backgroundColor,
        bottomBar = {
            BottomAppBar(
                containerColor = backgroundColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    menuItems.forEachIndexed { index, item ->
                        MenuItemContent(
                            item = item,
                            index = index,
                            selectedIndex = selectedIndex,
                            onMenuItemClick = {
                                selectedIndex = index
                                mainNavHost.navigate(
                                    it.route.route
                                )
                            }
                        )
                    }
                }
            }
        }
    ) {
        Box(
            modifier = modifier
                .padding(it)
        ) {
            val graph =
                mainNavHost.createGraph(startDestination = HomeScreenRoute.Analyse.route) {
                    composable(route = HomeScreenRoute.Analyse.route) {
                        AnalyseScreen(navController)
                    }
                    composable(route = HomeScreenRoute.Practice.route) {
                        PracticeScreen()
                    }
                    composable(route = HomeScreenRoute.Progress.route) {
                        ProgressScreen()
                    }
                    composable(route = HomeScreenRoute.History.route) {
                        HistoryScreen()
                    }
                }
            NavHost(
                navController = mainNavHost,
                graph = graph,
                modifier = Modifier
                    .background(backgroundColor)
            )
        }
    }
}

@Composable
fun MenuItemContent(
    index: Int,
    selectedIndex: Int,
    item: MenuItem,
    modifier: Modifier = Modifier,
    onMenuItemClick: (MenuItem) -> Unit = {}
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .bounceClick {
                    onMenuItemClick(item)
                }
                .size(54.dp)
                .background(
                    color = if (index == selectedIndex) lightGoldenColor else secondaryColor,
                    shape = CircleShape
                )
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = item.icon),
                contentDescription = item.label,
                modifier = Modifier
                    .align(Alignment.Center),
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = item.label,
            style = CustomTextStyle.copy(
                color = whiteColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
            ),
            modifier = Modifier
                .noInteractionClickable {
                    onMenuItemClick(item)
                }
        )
    }
}