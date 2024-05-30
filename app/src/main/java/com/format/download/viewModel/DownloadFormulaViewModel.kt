package com.format.download.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import arrow.core.getOrElse
import com.format.app.navigation.navigator.Navigator
import com.format.domain.formulas.repository.FormulasRepository
import com.format.domain.model.FormulaGroup
import com.format.download.viewState.DownloadFormulaViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DownloadFormulaViewModel(
    private val formulasRepository: FormulasRepository,
    private val navigator: Navigator,
) : ViewModel() {
    private val _uiState = MutableStateFlow(DownloadFormulaViewState(emptyList()))
    val uiState: LiveData<DownloadFormulaViewState>
        get() = _uiState.asLiveData()

    fun loadData() = viewModelScope.launch {
        val remoteFormulaGroups = formulasRepository.groups().getOrElse { emptyList() }
        _uiState.update { it.copy(remoteFormulaGroups) }
    }

    fun onDownloadFormulaClicked(remoteFormulaGroup: FormulaGroup) = viewModelScope.launch {
        formulasRepository.downloadFormulaGroup(remoteFormulaGroup.id).mapLeft { return@launch }
        formulasRepository.updateDownloadedFormulas(remoteFormulaGroup)
        navigator.goBack()
    }
}