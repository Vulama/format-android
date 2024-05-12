package com.format.common.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.format.app.theme.ColorPalette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForMatInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    TextField(
        value = value,
        onValueChange = { onValueChange(it) },
        label = label,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        visualTransformation = visualTransformation,
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = ColorPalette.Transparent,
            unfocusedIndicatorColor = ColorPalette.Transparent,
            disabledIndicatorColor = ColorPalette.Transparent,
            errorIndicatorColor = ColorPalette.Transparent,
        )
    )
}