package com.format.formulas.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.format.R
import com.format.app.theme.ColorPalette
import com.format.common.ui.ForMatInputField
import com.format.common.ui.NormalButton
import com.format.common.ui.VerticalSpacer
import com.format.domain.model.FormulaEntry
import com.format.domain.model.FormulaGroup
import com.format.formulas.viewModel.EditGroupViewModel
import com.ramcosta.composedestinations.annotation.Destination
import org.koin.androidx.compose.getViewModel

@Destination
@Composable
fun EditGroupScreen(
    formulaGroup: FormulaGroup? = null,
) {
    val viewModel: EditGroupViewModel = getViewModel()

    EditGroupScreenStateless(
        isNewFormulaGroup = formulaGroup == null,
        formulaGroup = formulaGroup ?: FormulaGroup("", listOf(FormulaEntry())),
        onAddGroupClicked = { groupName, formulas -> viewModel.addLocalGroup(groupName, formulas) },
        onUpdateFormulaGroup = { groupName, formulas -> viewModel.updateGroup(groupName, formulas, formulaGroup) }
    )
}


@Composable
fun EditGroupScreenStateless(
    isNewFormulaGroup: Boolean,
    formulaGroup: FormulaGroup,
    onAddGroupClicked: (String, List<FormulaEntry>) -> Unit,
    onUpdateFormulaGroup: (String, List<FormulaEntry>) -> Unit,
) {
    var groupName by remember { mutableStateOf(formulaGroup.name) }
    var formulas by remember { mutableStateOf(formulaGroup.formulas) }

    Box(modifier = Modifier.fillMaxSize()) {
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

            Text(
                text = stringResource(id = R.string.edit_group_screen_title),
                style = MaterialTheme.typography.headlineLarge.copy(ColorPalette.Primary),
            )

            VerticalSpacer(distance = 24.dp)

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.edit_group_screen_group_title),
                style = MaterialTheme.typography.titleLarge.copy(ColorPalette.Primary),
            )

            ForMatInputField(
                value = groupName,
                onValueChange = { groupName = it },
                label = { Text("Formula Group Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            formulas.forEachIndexed { outerIndex, entry ->
                FormulaEntryInput(
                    entry = entry,
                    index = outerIndex,
                    onEntryChange = { formulaEntry ->
                        formulas = formulas.mapIndexed { index, formula ->
                            if (index != outerIndex) formula
                            else formulaEntry
                        }
                    },
                    onRemoveClick = {
                        formulas = formulas.filterIndexed { index, _ -> index != outerIndex }
                    }
                )
            }

            NormalButton(
                onClick = {
                    formulas = formulas.plus(FormulaEntry())
                }
            ) {
                Text(text = stringResource(id = R.string.edit_group_screen_add_formula_button_label))
            }
        }

        if (isNewFormulaGroup) {
            NormalButton(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp),
                onClick = { onAddGroupClicked(groupName, formulas) },
                enabled = groupName != ""
            ) {
                Text(
                    text = stringResource(id = R.string.edit_group_screen_create_group_button_label),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        } else {
            NormalButton(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp),
                onClick = { onUpdateFormulaGroup(groupName, formulas) },
                enabled = groupName != ""
            ) {
                Text(
                    text = stringResource(id = R.string.edit_group_screen_update_group_button_label),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}

@Composable
fun FormulaEntryInput(
    entry: FormulaEntry,
    onEntryChange: (FormulaEntry) -> Unit,
    onRemoveClick: () -> Unit,
    index: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.edit_group_screen_formula_label, index + 1),
            style = MaterialTheme.typography.titleSmall.copy(ColorPalette.Secondary),
        )

        VerticalSpacer(distance = 8.dp)

        ForMatInputField(
            modifier = Modifier.fillMaxWidth(),
            value = entry.title,
            onValueChange = { onEntryChange(entry.copy(title = it)) },
            label = { Text("Formula Name") }
        )

        VerticalSpacer(distance = 8.dp)

        ForMatInputField(
            modifier = Modifier.fillMaxWidth(),
            value = entry.formula,
            onValueChange = { onEntryChange(entry.copy(formula = it)) },
            label = { Text("Math Formula (LaTeX)") }
        )

        VerticalSpacer(distance = 8.dp)

        ForMatInputField(
            modifier = Modifier.fillMaxWidth(),
            value = entry.description,
            onValueChange = { onEntryChange(entry.copy(description = it)) },
            label = { Text("Description") }
        )


        NormalButton(
            onClick = { onRemoveClick() },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(
                text = stringResource(id = R.string.edit_group_screen_remove_formula),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}


@Preview
@Composable
fun PreviewEditGroupScreen() {
    EditGroupScreenStateless(
        true,
        FormulaGroup("", listOf(FormulaEntry())),
        { _, _ -> },
        { _, _ -> },
    )
}



