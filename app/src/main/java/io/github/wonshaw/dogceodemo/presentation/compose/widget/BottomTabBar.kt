package io.github.wonshaw.dogceodemo.presentation.compose.widget

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@Composable
fun BottomTabBar(
    currentRoute: String?,
    tabInfoList: List<BottomTabBarItem>,
    onTabSelected: (String) -> Unit = {},
) {
    NavigationBar {
        tabInfoList.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = stringResource(id = item.label)
                    )
                },
                label = { Text(text = stringResource(id = item.label)) },
                selected = selected,
                onClick = { onTabSelected(item.route) },
            )
        }
    }
}