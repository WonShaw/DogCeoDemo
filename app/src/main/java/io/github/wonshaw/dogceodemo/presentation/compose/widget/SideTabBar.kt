package io.github.wonshaw.dogceodemo.presentation.compose.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@Composable
fun SideTabBar(
    currentRoute: String?,
    paddingValues: PaddingValues,
    tabInfoList: List<BottomTabBarItem>,
    onTabSelected: (String) -> Unit = {},
) {
    NavigationRail(
        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer).padding(
            start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
            end = 8.dp,
        ),
        containerColor = Color.Transparent,
    ) {
        tabInfoList.forEach { item ->
            val selected = currentRoute == item.route
            NavigationRailItem(
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