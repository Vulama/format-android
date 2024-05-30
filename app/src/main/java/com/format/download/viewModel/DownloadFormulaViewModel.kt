package com.format.download.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import arrow.core.getOrElse
import com.format.app.navigation.navigator.Navigator
import com.format.common.infrastructure.analytics.AnalyticsService
import com.format.common.infrastructure.logger.Logger
import com.format.common.model.AnalyticsEvent
import com.format.common.model.AnalyticsScreen
import com.format.domain.formulas.repository.FormulasRepository
import com.format.domain.model.ApplicationFlows
import com.format.domain.model.FormulaGroup
import com.format.download.viewState.DownloadFormulaViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DownloadFormulaViewModel(
    private val formulasRepository: FormulasRepository,
    private val navigator: Navigator,
    private val analyticsService: AnalyticsService,
    private val logger: Logger,
) : ViewModel() {
    private val _uiState = MutableStateFlow(DownloadFormulaViewState(emptyList()))
    val uiState: LiveData<DownloadFormulaViewState>
        get() = _uiState.asLiveData()

    fun loadData() = viewModelScope.launch {
        logger.i(ApplicationFlows.Download, "Loading data started")
        analyticsService.trackScreen(AnalyticsScreen.DownloadScreen)
        val remoteFormulaGroups = formulasRepository.groups().getOrElse { emptyList() }
        logger.i(ApplicationFlows.Download, "Loading data finished")
        _uiState.update { it.copy(remoteFormulaGroups) }
    }

    fun onDownloadFormulaClicked(remoteFormulaGroup: FormulaGroup) = viewModelScope.launch {
        formulasRepository.downloadFormulaGroup(remoteFormulaGroup.id).mapLeft {
            logger.w(ApplicationFlows.Download, "Downloaded group failed to link to user, aborting download: ${it.message}")
            return@launch
        }
        formulasRepository.updateDownloadedFormulas(remoteFormulaGroup)
        analyticsService.trackEvent(AnalyticsEvent.FormulaDownloaded(remoteFormulaGroup.id))
        logger.i(ApplicationFlows.Download, "Group download is successful")
        navigator.goBack()
    }
}