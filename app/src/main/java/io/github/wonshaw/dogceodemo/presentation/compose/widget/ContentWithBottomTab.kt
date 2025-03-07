package io.github.wonshaw.dogceodemo.presentation.compose.widget

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.MutableWindowInsets
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp


class BottomTabBarItem(
    val route: String,
    @StringRes val label: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ContentWithBottomTab(
    contentWindowInsets: WindowInsets = WindowInsets.systemBars.union(WindowInsets.displayCutout),
    contentArea: @Composable (paddingValues: PaddingValues) -> Unit,
    bottomArea: @Composable () -> Unit,
) {
    val safeInsets = remember(contentWindowInsets) {
        MutableWindowInsets(
            contentWindowInsets
        )
    }
    val paddingValues = safeInsets.asPaddingValues()
    Column {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            contentArea(
                PaddingValues(
                    start = 0.dp,
                    top = paddingValues.calculateTopPadding(),
                    end = 0.dp,
                    bottom = 0.dp
                )
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            bottomArea()
        }
    }
}