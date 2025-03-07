package io.github.wonshaw.dogceodemo.presentation.compose.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.MutableWindowInsets
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ContentWithSideTab(
    contentWindowInsets: WindowInsets = WindowInsets.systemBars.union(WindowInsets.displayCutout),
    contentArea: @Composable (paddingValues: PaddingValues) -> Unit,
    sideArea: @Composable (paddingValues: PaddingValues) -> Unit,
) {
    val safeInsets = remember(contentWindowInsets) {
        MutableWindowInsets(
            contentWindowInsets
        )
    }
    val layoutDirection = LocalLayoutDirection.current

    val paddingValues = safeInsets.asPaddingValues()

    Row {
        Box(
            modifier = Modifier
                .fillMaxHeight(),
        ) {
            sideArea(
                PaddingValues(
                    start = paddingValues.calculateStartPadding(layoutDirection),
                    top = paddingValues.calculateTopPadding(),
                    end = 0.dp,
                    bottom = paddingValues.calculateBottomPadding(),
                )
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
        ) {
            contentArea(
                PaddingValues(
                    start = 0.dp,
                    top = paddingValues.calculateTopPadding(),
                    end = paddingValues.calculateEndPadding(layoutDirection),
                    bottom = paddingValues.calculateBottomPadding(),
                )
            )
        }
    }
}