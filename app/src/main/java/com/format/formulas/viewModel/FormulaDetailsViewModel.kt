package com.format.formulas.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.format.common.infrastructure.analytics.AnalyticsService
import com.format.common.infrastructure.logger.Logger
import com.format.common.model.AnalyticsScreen
import com.format.common.model.Epoch
import com.format.data.infrastructure.dateTime.DateTimeProvider
import com.format.data.networking.token.TokenStore
import com.format.domain.formulas.repository.FormulasRepository
import com.format.domain.model.ApplicationFlows
import com.format.domain.model.FormulaEntry
import com.format.domain.model.Reaction
import com.format.formulas.viewState.FormulaDetailsViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FormulaDetailsViewModel(
    private val tokenStore: TokenStore,
    private val dateTimeProvider: DateTimeProvider,
    private val formulasRepository: FormulasRepository,
    private val analyticsService: AnalyticsService,
    private val logger: Logger,
) : ViewModel() {
    private val _uiState = MutableStateFlow(FormulaDetailsViewState(false, null))
    val uiState: LiveData<FormulaDetailsViewState>
        get() = _uiState.asLiveData()

    init {
        val refreshTokenIsValid = tokenStore.get().refreshToken.expiresAt > dateTimeProvider.now()
        _uiState.update { it.copy(refreshTokenIsValid) }
    }

    fun loadData(formulaId: Int) {
        logger.i(ApplicationFlows.Formula, "Formula data loading started")
        analyticsService.trackScreen(AnalyticsScreen.FormulaPreviewScreen)
        val userReactions = formulasRepository.userReactions()
        val formulaReaction = userReactions.firstOrNull { it.formulaId == formulaId }
        logger.i(ApplicationFlows.Formula, "Formula data loading finished")
        _uiState.update { it.copy(reaction = formulaReaction) }
    }

    fun onSuccessClicked(formulaEntry: FormulaEntry) = viewModelScope.launch {
        formulasRepository.react(formulaEntry, true)
        logger.i(ApplicationFlows.Formula, "User reacted positively on formula ${formulaEntry.id}")
        _uiState.update { it.copy(reaction = Reaction(true, Epoch.None, formulaEntry.id)) }
    }

    fun onFailureClicked(formulaEntry: FormulaEntry) = viewModelScope.launch {
        formulasRepository.react(formulaEntry, false)
        logger.i(ApplicationFlows.Formula, "User reacted positively on formula ${formulaEntry.id}")
        _uiState.update { it.copy(reaction = Reaction(false, Epoch.None, formulaEntry.id)) }
    }
}