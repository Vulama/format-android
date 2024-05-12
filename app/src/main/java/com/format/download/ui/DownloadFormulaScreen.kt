package com.format.download.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.format.app.theme.ColorPalette
import com.format.common.ui.FormulaDivider
import com.format.common.ui.NormalButton
import com.format.domain.model.FormulaGroup
import com.format.download.viewModel.DownloadFormulaViewModel
import com.ramcosta.composedestinations.annotation.Destination
import org.koin.androidx.compose.getViewModel

@Destination
@Composable
fun DownloadFormulaScreen() {
    val viewModel: DownloadFormulaViewModel = getViewModel()
    val viewState = viewModel.uiState.observeAsState().value

    viewState?.let {
        DownloadFormulaScreenStateless(
            formulas = viewState.remoteFormulaGroups,
            onDownloadFormulaClicked = { viewModel.onDownloadFormulaClicked(it) }
        )
    }

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }
}

@Composable
fun DownloadFormulaScreenStateless(
    formulas: List<FormulaGroup>,
    onDownloadFormulaClicked: (FormulaGroup) -> Unit
) {
    var selectedIndex by remember { mutableIntStateOf(-1) }

    Box(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxSize()
    ) {
        Text(
            text = "Download Formula",
            style = MaterialTheme.typography.headlineLarge.copy(
                color = ColorPalette.Primary,
                fontWeight = FontWeight.Bold,
            )
        )

        LazyColumn(
            modifier = Modifier.padding(top = 70.dp, bottom = 50.dp)
        ) {
            itemsIndexed(formulas) { index, item ->
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium.copy(ColorPalette.OnSecondaryContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (index != selectedIndex) {
                                ColorPalette.SecondaryContainer.copy(0.4f)
                            } else {
                                ColorPalette.PrimaryContainer
                            },
                            RoundedCornerShape(16.dp)
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { if (selectedIndex == index) selectedIndex = -1 else selectedIndex = index }
                        .padding(16.dp)
                )

                if (index != formulas.size - 1) {
                    FormulaDivider(10.dp)
                }
            }
        }

        NormalButton(
            onClick = { onDownloadFormulaClicked(formulas[selectedIndex]) },
            enabled = selectedIndex != -1,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Text(text = "Download Selected Formula")
        }
    }
}

@Composable
@Preview
fun PreviewDownloadFormulaScreen(){
    DownloadFormulaScreenStateless(
        formulas = listOf(
            FormulaGroup("formula group1", emptyList()),
            FormulaGroup("formula group2", emptyList()),
        ),
        onDownloadFormulaClicked = { }
    )
}