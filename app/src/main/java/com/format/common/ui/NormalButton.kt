package com.format.common.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.format.app.theme.ColorPalette

@Composable
fun NormalButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.shape,
    colors: ForMatButtonColors = ForMatButtonColors.Default(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    isLoading: Boolean = false,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit
) {
    val containerColor = colors.containerColor(enabled)
    val contentColor = colors.contentColor(enabled)
    val shadowElevation = 0.dp
    val tonalElevation = 0.dp

    Surface(
        onClick = onClick,
        modifier = modifier.semantics { role = Role.Button },
        enabled = enabled,
        shape = shape,
        color = containerColor,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
        border = border,
        interactionSource = interactionSource,
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            ProvideTextStyle(value = MaterialTheme.typography.labelLarge) {
                Row(
                    Modifier
                        .defaultMinSize(
                            minWidth = ButtonDefaults.MinWidth,
                            minHeight = ButtonDefaults.MinHeight
                        )
                        .padding(contentPadding),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    content()

                    if (isLoading) {
                        HorizontalSpacer(distance = 12.dp)

                        Spinner(
                            size = 16.dp,
                            stroke = 2.dp,
                            trackColor = ColorPalette.Surface,
                            color = ColorPalette.Outline,
                        )
                    }
                }
            }
        }
    }
}

interface ForMatButtonColors {
    val containerColor: Color
    val contentColor: Color
    val disabledContainerColor: Color
    val disabledContentColor: Color

    data class Default(
        override val containerColor: Color = ColorPalette.Primary,
        override val contentColor: Color = ColorPalette.OnPrimary,
        override val disabledContainerColor: Color = ColorPalette.Primary.copy(0.2f),
        override val disabledContentColor: Color = ColorPalette.OnPrimary.copy(0.2f),
    ) : ForMatButtonColors

    data class Alert(
        override val containerColor: Color = ColorPalette.ErrorContainer,
        override val contentColor: Color = ColorPalette.Error,
        override val disabledContainerColor: Color = ColorPalette.ErrorContainer.copy(0.2f),
        override val disabledContentColor: Color = ColorPalette.OnErrorContainer.copy(0.2f),
    ) : ForMatButtonColors

    fun containerColor(enabled: Boolean) = if (enabled) containerColor else disabledContainerColor
    fun contentColor(enabled: Boolean) = if (enabled) contentColor else disabledContentColor
}



@Preview
@Composable
fun PreviewNormalButton(){
    NormalButton(
        onClick = {  },
        colors = ForMatButtonColors.Alert(),
    ) {
        Text(text = "Test Test")
    }
}
