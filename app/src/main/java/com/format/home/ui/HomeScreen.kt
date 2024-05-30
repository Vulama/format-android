package com.format.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.format.R
import com.format.app.theme.ColorPalette
import com.format.common.ui.FormulaDivider
import com.format.common.ui.NormalButton
import com.format.common.ui.VerticalSpacer
import com.format.domain.model.FormulaGroup
import com.format.home.viewModel.HomeViewModel
import com.ramcosta.composedestinations.annotation.Destination
import org.koin.androidx.compose.getViewModel

@Destination
@Composable
fun HomeScreen() {
    val viewModel: HomeViewModel = getViewModel()
    val viewState = viewModel.uiState.observeAsState().value

    viewState?.let {
        HomeScreenStateless(
            favouriteFormulas = viewState.favouriteFormulas,
            localFormulas = viewState.localFormulas,
            remoteFormulas = viewState.remoteFormulas,
            onAddFormulaClick = { viewModel.onAddFormulasClicked() },
            onDownloadFormulaClick = { viewModel.onDownloadFormulasClicked() },
            onFormulaGroupClicked = { viewModel.onFormulaGroupClicked(it) },
            onLogoutClicked = { viewModel.onLogoutClicked() },
        )
    }

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }
}

@Composable
fun HomeScreenStateless(
    favouriteFormulas: List<FormulaGroup>,
    localFormulas: List<FormulaGroup>,
    remoteFormulas: List<FormulaGroup>,
    onAddFormulaClick: () -> Unit,
    onDownloadFormulaClick: () -> Unit,
    onFormulaGroupClicked: (FormulaGroup) -> Unit,
    onLogoutClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .padding(bottom = 24.dp, top = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(id = R.string.main_screen_title),
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = ColorPalette.Primary,
                    fontWeight = FontWeight.Bold,
                )
            )

            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = null,
                tint = ColorPalette.Primary,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onLogoutClicked() },
            )
        }

        if (favouriteFormulas.isNotEmpty()) {
            SectionWithTitle(title = stringResource(id = R.string.main_screen_favourite_group_title)) {
                FormulaList(favouriteFormulas, onFormulaGroupClicked)
            }

            if (localFormulas.isNotEmpty() || remoteFormulas.isNotEmpty()) {
                FormulaDivider()
            }
        }

        if (localFormulas.isNotEmpty()) {
            SectionWithTitle(title = stringResource(id = R.string.main_screen_local_group_title)) {
                FormulaList(localFormulas, onFormulaGroupClicked)
            }

            if (remoteFormulas.isNotEmpty()) {
                FormulaDivider()
            }
        }

        if (remoteFormulas.isNotEmpty()) {
            SectionWithTitle(title = stringResource(id = R.string.main_screen_remote_group_title)) {
                FormulaList(remoteFormulas, onFormulaGroupClicked)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        NormalButton(
            onClick = { onDownloadFormulaClick() },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 8.dp),
        ) {
            Text(text = stringResource(id = R.string.main_screen_download_button_label))
        }

        NormalButton(
            onClick = { onAddFormulaClick() },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = stringResource(id = R.string.main_screen_add_group_label))
        }
    }
}

@Composable
fun SectionWithTitle(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorPalette.SecondaryContainer.copy(0.4f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(ColorPalette.OnSecondaryContainer),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        VerticalSpacer(distance = 8.dp)

        content()
    }
}

@Composable
fun FormulaList(
    formulas: List<FormulaGroup>,
    onFormulaGroupClicked: (FormulaGroup) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        formulas.forEachIndexed { index, formula ->
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ColorPalette.SecondaryContainer, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onFormulaGroupClicked(formula) }
                    .padding(16.dp),
                text = formula.name,
                style = MaterialTheme.typography.bodyLarge,
            )

            if (index != formulas.size - 1) {
                VerticalSpacer(distance = 8.dp)
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreenStateless(
        favouriteFormulas = listOf(FormulaGroup("Formula A", emptyList())),
        localFormulas = listOf(FormulaGroup("Formula A", emptyList()), FormulaGroup("Formula B", emptyList())),
        remoteFormulas = listOf(FormulaGroup("Formula A", emptyList()), FormulaGroup("Formula B", emptyList())),
        onAddFormulaClick = {},
        onDownloadFormulaClick = {},
        onFormulaGroupClicked = {},
        onLogoutClicked = {},
    )
}
