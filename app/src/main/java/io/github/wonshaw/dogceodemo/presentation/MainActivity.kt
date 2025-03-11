package io.github.wonshaw.dogceodemo.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.wonshaw.dogceodemo.R
import io.github.wonshaw.dogceodemo.presentation.compose.theme.DogCeoDemoTheme
import io.github.wonshaw.dogceodemo.presentation.compose.widget.BottomTabBar
import io.github.wonshaw.dogceodemo.presentation.compose.widget.BottomTabBarItem
import io.github.wonshaw.dogceodemo.presentation.compose.widget.ContentWithBottomTab
import io.github.wonshaw.dogceodemo.presentation.compose.widget.ContentWithSideTab
import io.github.wonshaw.dogceodemo.presentation.compose.widget.SideTabBar
import io.github.wonshaw.dogceodemo.presentation.gallery.GalleryScreen
import io.github.wonshaw.dogceodemo.presentation.quiz.QuizScreenWithViewModel
import io.github.wonshaw.dogceodemo.presentation.settings.SettingsScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)

            DogCeoDemoTheme {
                val tabInfoList = remember {
                    getTabInfos()
                }

                MainScreen(
                    windowSizeClass = windowSizeClass,
                    tabInfoList = tabInfoList,
                )
            }
        }
    }

}

private fun getTabInfos(): List<BottomTabBarItem> {
    return listOf(
        BottomTabBarItem(
            route = ROUTE_QUIZ,
            label = R.string.label_quiz,
            selectedIcon = Icons.Filled.SportsEsports,
            unselectedIcon = Icons.Outlined.SportsEsports
        ), BottomTabBarItem(
            route = ROUTE_GALLERY,
            label = R.string.label_gallery,
            selectedIcon = Icons.Filled.Collections,
            unselectedIcon = Icons.Outlined.Collections
        ), BottomTabBarItem(
            route = ROUTE_SETTINGS,
            label = R.string.label_settings,
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings
        )
    )
}

@Composable
private fun MainScreen(
    windowSizeClass: WindowSizeClass,
    tabInfoList: List<BottomTabBarItem>,
) {
    val navController = rememberNavController()
    if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) {
        MainScreenForPortrait(navController, tabInfoList)
    } else {
        MainScreenForLandscape(navController, tabInfoList)
    }
}

@Composable
private fun MainScreenForLandscape(
    navController: NavHostController,
    tabInfoList: List<BottomTabBarItem>
) {
    ContentWithSideTab(
        contentArea = { paddingValues ->
            MainContentHost(navController = navController, paddingValues = paddingValues, landscape = true)
        },
        sideArea = { paddingValues ->
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            SideTabBar(
                currentRoute = navBackStackEntry?.destination?.route,
                paddingValues = paddingValues,
                tabInfoList = tabInfoList,
                onTabSelected = { route ->
                    navController.navigate(route) {
                        popUpTo(0) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    )
}

@Composable
private fun MainScreenForPortrait(
    navController: NavHostController,
    tabInfoList: List<BottomTabBarItem>
) {
    ContentWithBottomTab(
        contentArea = { paddingValues ->
            MainContentHost(navController = navController, paddingValues = paddingValues)
        },
        bottomArea = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            BottomTabBar(
                currentRoute = navBackStackEntry?.destination?.route,
                tabInfoList = tabInfoList,
                onTabSelected = { route ->
                    navController.navigate(route) {
                        popUpTo(0) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    )
}

@Composable
private fun MainContentHost(navController: NavHostController, paddingValues: PaddingValues, landscape: Boolean = false) {
    NavHost(
        navController = navController,
        startDestination = ROUTE_QUIZ,
        Modifier.padding(paddingValues),
    ) {
        composable(route = ROUTE_QUIZ) { QuizScreenWithViewModel(landscape) }
        composable(route = ROUTE_GALLERY) { GalleryScreen() }
        composable(route = ROUTE_SETTINGS) { SettingsScreen() }
    }
}

// HiltViewModel exists in a sub-layout, making preview unavailable.

//@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
//@Preview(showBackground = true, name = "normal")
//@Composable
//fun MainTabPreview() {
//    DogCeoDemoTheme {
//        MainScreen(
//            tabInfoList = getTabInfos(),
//            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(360.dp, 640.dp)),
//        )
//    }
//}
//
//@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
//@Preview(showBackground = true, name = "landscape")
//@Composable
//fun MainTabLandScapePreview() {
//    DogCeoDemoTheme {
//        MainScreen(
//            tabInfoList = getTabInfos(),
//            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(640.dp, 360.dp)),
//        )
//    }
//}

private const val ROUTE_QUIZ = "quiz"
private const val ROUTE_GALLERY = "gallery"
private const val ROUTE_SETTINGS = "settings"