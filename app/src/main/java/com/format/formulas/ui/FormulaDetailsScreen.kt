package com.format.formulas.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.format.R
import com.format.app.theme.ColorPalette
import com.format.common.ui.AnalyticsTable
import com.format.common.ui.ForMatButtonColors
import com.format.common.ui.HorizontalSpacer
import com.format.common.ui.LatexView
import com.format.common.ui.NormalButton
import com.format.common.ui.VerticalSpacer
import com.format.domain.model.FormulaEntry
import com.format.domain.model.Reaction
import com.format.formulas.viewModel.FormulaDetailsViewModel
import com.ramcosta.composedestinations.annotation.Destination
import org.koin.androidx.compose.getViewModel

@Destination
@Composable
fun FormulaDetailsScreen(
    formulaEntry: FormulaEntry,
    successCount: Int,
    failureCount: Int,
    showStatistic: Boolean,
) {
    val viewModel: FormulaDetailsViewModel = getViewModel()
    val viewState = viewModel.uiState.observeAsState().value

    FormulaDetailsScreenStateless(
        formulaEntry = formulaEntry,
        onSuccessClicked = { viewModel.onSuccessClicked(formulaEntry) },
        onFailureClicked = { viewModel.onFailureClicked(formulaEntry) },
        successCount = successCount,
        failureCount = failureCount,
        showStatistic = showStatistic,
        areReactionsEnabled = viewState?.areReactionsEnabled ?: false,
        reaction = viewState?.reaction,
    )

    LaunchedEffect(Unit) {
        viewModel.loadData(formulaEntry.id)
    }
}

@Composable
private fun FormulaDetailsScreenStateless(
    formulaEntry: FormulaEntry,
    onSuccessClicked: () -> Unit,
    onFailureClicked: () -> Unit,
    successCount: Int,
    failureCount: Int,
    showStatistic: Boolean,
    areReactionsEnabled: Boolean,
    reaction: Reaction?,
) {
    Column(
        modifier = Modifier
            .background(ColorPalette.Surface)
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = formulaEntry.title,
            style = MaterialTheme.typography.displayMedium.copy(ColorPalette.Primary),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            textAlign = TextAlign.Center,

            )

        VerticalSpacer(distance = 36.dp)

        LatexView(
            latexText = formulaEntry.formula,
            contentScale = ContentScale.FillWidth,
        )

        VerticalSpacer(distance = 36.dp)

        Text(
            text = formulaEntry.description,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        VerticalSpacer(distance = 24.dp)

        if (!showStatistic && formulaEntry.id != -1) {

            Text(
                text = stringResource(id = R.string.formula_screen_reaction_title),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )

            VerticalSpacer(distance = 12.dp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NormalButton(
                    onClick = onSuccessClicked,
                    modifier = Modifier.weight(1f),
                    enabled = areReactionsEnabled && reaction?.type != true,
                ) {
                    Text(text = stringResource(id = R.string.formula_screen_reaction_positive_label))
                }

                HorizontalSpacer(distance = 8.dp)

                NormalButton(
                    onClick = onFailureClicked,
                    modifier = Modifier.weight(1f),
                    colors = ForMatButtonColors.Alert(),
                    enabled = areReactionsEnabled && reaction?.type != false,
                ) {
                    Text(text = stringResource(id = R.string.formula_screen_reaction_negative_label))
                }
            }
        } else if (showStatistic) {
            AnalyticsTable(
                successCount = successCount,
                failureCount = failureCount,
            )
        }
    }
}

@Composable
@Preview
private fun PreviewFormulaDetailsScreen() {
    FormulaDetailsScreenStateless(
        FormulaEntry(
            "Test title",
            "",
            "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum."
        ),
        {},
        {},
        10,
        5,
        true,
        true,
        null,
    )
}