package com.format.common.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
fun VerticalSpacer(distance: Dp) {
    Spacer(modifier = Modifier.height(distance))
}

@Composable
fun HorizontalSpacer(distance: Dp) {
    Spacer(modifier = Modifier.width(distance))
}
