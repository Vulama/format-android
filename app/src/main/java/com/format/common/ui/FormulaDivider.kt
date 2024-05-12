package com.format.common.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.format.app.theme.ColorPalette

@Composable
fun FormulaDivider(
    verticalPadding: Dp = 20.dp,
) {
    Box(
        modifier = Modifier
            .padding(vertical = verticalPadding)
            .height(1.dp)
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(
                    listOf(
                        ColorPalette.Transparent,
                        ColorPalette.PrimaryContainer,
                        ColorPalette.PrimaryContainer,
                        ColorPalette.PrimaryContainer,
                        ColorPalette.Transparent,
                    )
                )
            )

    )
}