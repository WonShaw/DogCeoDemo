package io.github.wonshaw.dogceodemo.presentation.compose.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha

@Composable
fun TabHost(
    pageId: Int = 0,
    paddingValues: PaddingValues,
    composableFactory: @Composable (Int) -> Unit = {},
) {
    val seenIds = remember { mutableSetOf<Int>() }

    seenIds.add(pageId)

    seenIds.forEach { cachedId ->
        val isCurrentTab = cachedId == pageId
        Box(
            modifier = Modifier
                .padding(paddingValues = paddingValues)
                .alpha(if (isCurrentTab) 1F else 0F)
        ) {
            composableFactory(cachedId)
        }
    }
}