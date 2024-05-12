package com.format.common.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.format.app.theme.ColorPalette

@Composable
fun Spinner(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    stroke: Dp = 6.dp,
    color: Color? = null,
    trackColor: Color? = null,
) {
    BasicSpinner(
        modifier = modifier.size(size),
        trackColor = trackColor ?: ColorPalette.OnPrimary,
        spinnerColor = color ?: ColorPalette.Primary,
        stroke = stroke
    )
}

@Composable
fun BasicSpinner(
    modifier: Modifier,
    stroke: Dp,
    trackColor: Color,
    spinnerColor: Color,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "spinner-transition")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1_000,
                easing = LinearEasing
            )
        ),
        label = "rotation-angle"
    )
    val strokeWidth = with(LocalDensity.current) { stroke.toPx() }
    Canvas(modifier = modifier) {
        // Track
        drawArc(
            color = trackColor,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth)
        )
        // Spinner
        drawArc(
            color = spinnerColor,
            startAngle = rotation,
            sweepAngle = 45f,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}