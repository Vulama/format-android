package com.format.formulas.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.format.R
import com.format.app.theme.ColorPalette
import com.format.common.ui.AnalyticsTable
import com.format.common.ui.ForMatButtonColors
import com.format.common.ui.HorizontalSpacer
import com.format.common.ui.LatexView
import com.format.common.ui.NormalButton
import com.format.common.ui.VerticalSpacer
import com.format.domain.model.FormulaEntry
import com.format.domain.model.FormulaGroup
import com.format.domain.model.Reaction
import com.format.formulas.viewModel.GroupDetailsViewModel
import com.ramcosta.composedestinations.annotation.Destination
import org.koin.androidx.compose.getViewModel

@Destination
@Composable
fun GroupDetailsScreen(
    formulaGroup: FormulaGroup,
) {
    val viewModel: GroupDetailsViewModel = getViewModel()
    val viewState = viewModel.uiState.observeAsState().value

    var isFavourite by remember { mutableStateOf(formulaGroup.isFavourite) }

    GroupDetailsScreenStateless(
        groupTitle = formulaGroup.name,
        formulas = formulaGroup.formulas,
        isLocal = formulaGroup.id == -1,
        isFavourite = isFavourite,
        isGroupPublished = viewState?.isGroupPublished ?: true,
        isPublishInProgress = viewState?.isPublishInProgress ?: false,
        onPublishClicked = { viewModel.publishFormulaGroup(formulaGroup) },
        onFavouriteToggled = {
            isFavourite = !isFavourite
            viewModel.onFavouriteToggled(formulaGroup)
        },
        onEditGroupClicked = { viewModel.onEditGroupClicked(formulaGroup) },
        onDeleteGroup = { viewModel.onDeleteGroupClicked(formulaGroup) },
        onFormulaClicked = { viewModel.onFormulaClicked(it) },
        reactions = viewState?.groupReactions
    )

    LaunchedEffect(Unit) {
        viewModel.loadReactions(formulaGroup)
    }
}

@Composable
fun GroupDetailsScreenStateless(
    reactions: List<Reaction>?,
    groupTitle: String,
    formulas: List<FormulaEntry>,
    isLocal: Boolean,
    isFavourite: Boolean,
    onPublishClicked: () -> Unit,
    onFavouriteToggled: () -> Unit,
    isGroupPublished: Boolean,
    isPublishInProgress: Boolean,
    onEditGroupClicked: () -> Unit,
    onDeleteGroup: () -> Unit,
    onFormulaClicked: (FormulaEntry) -> Unit,
) {

    Column(
        modifier = Modifier
            .padding(24.dp)
            .padding(bottom = 56.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        VerticalSpacer(distance = 16.dp)

        Box(
            Modifier.fillMaxWidth()
        ) {
            Text(
                text = groupTitle,
                style = MaterialTheme.typography.headlineLarge.copy(ColorPalette.Primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(horizontal = 30.dp),
                textAlign = TextAlign.Center,
            )

            IconButton(
                onClick = { onFavouriteToggled() },
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterEnd),
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Favorite",
                    tint = if (isFavourite) ColorPalette.Primary else ColorPalette.PrimaryContainer
                )
            }
        }

        VerticalSpacer(distance = 24.dp)

        Column {
            formulas.forEachIndexed { index, item ->
                FormulaItem(formula = item, onFormulaClicked)

                if (index != formulas.size - 1) {
                    VerticalSpacer(distance = 8.dp)
                }
            }
        }

        if (!reactions.isNullOrEmpty()) {
            AnalyticsTable(
                reactions.count { it.type },
                reactions.count { !it.type }
            )
        }

        if (isLocal) {
            Row {
                NormalButton(
                    onClick = { onPublishClicked() },
                    modifier = Modifier.padding(top = 16.dp),
                    enabled = !isPublishInProgress && !isGroupPublished,
                    isLoading = isPublishInProgress,
                ) {
                    Text(text = stringResource(id = R.string.group_screen_publish_button_label))
                }

                HorizontalSpacer(distance = 16.dp)

                NormalButton(
                    onClick = onEditGroupClicked,
                    modifier = Modifier.padding(top = 16.dp),
                ) {
                    Text(text = stringResource(id = R.string.group_screen_edit_button_label))
                }
            }

        }

        NormalButton(
            onClick = { onDeleteGroup() },
            modifier = Modifier.padding(top = 8.dp),
            colors = ForMatButtonColors.Alert()
        ) {
            Text(text = stringResource(id = R.string.group_screen_remove_button_label))
        }

        VerticalSpacer(distance = 24.dp)
    }
}

@Composable
fun FormulaItem(
    formula: FormulaEntry,
    onFormulaClicked: (FormulaEntry) -> Unit,
) {
    Column(
        modifier = Modifier
            .background(ColorPalette.SecondaryContainer.copy(0.2f), RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .clickable { onFormulaClicked(formula) }
            .padding(horizontal = 8.dp)
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(
            modifier = Modifier,
            text = formula.title,
            style = MaterialTheme.typography.titleLarge.copy(ColorPalette.Primary),
        )

        VerticalSpacer(distance = 8.dp)

        LatexView(
            latexText = formula.formula,
            modifier = Modifier.height(24.dp),
        )
    }
}

@Preview
@Composable
fun PreviewGroupDetailsScreen() {
    GroupDetailsScreenStateless(
        emptyList(),
        "Test",
        listOf(FormulaEntry("Moja prva formula", "a + b"), FormulaEntry("Ovo je druga formula", "a + b")),
        true,
        true,
        {},
        {},
        true,
        false,
        {},
        {},
        {},
    )
}
