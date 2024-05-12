package com.format.formulas.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import arrow.core.getOrElse
import com.format.app.navigation.navigator.Navigator
import com.format.common.model.Epoch
import com.format.data.api.RestrictedApi
import com.format.destinations.EditGroupScreenDestination
import com.format.destinations.FormulaDetailsScreenDestination
import com.format.domain.formulas.repository.FormulasRepository
import com.format.domain.formulas.store.FormulaStore
import com.format.domain.model.FormulaEntry
import com.format.domain.model.FormulaGroup
import com.format.domain.model.Reaction
import com.format.formulas.viewState.GroupDetailsViewState
import com.format.home.viewState.HomeViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GroupDetailsViewModel(
    private val formulaStore: FormulaStore,
    private val formulasRepository: FormulasRepository,
    private val navigator: Navigator,
) : ViewModel() {
    private val _uiState = MutableStateFlow(GroupDetailsViewState())
    val uiState: LiveData<GroupDetailsViewState>
        get() = _uiState.asLiveData()

    fun loadReactions(formulaGroup: FormulaGroup) = viewModelScope.launch {
        val reactions = formulasRepository.getReactions(formulaGroup).getOrElse { null }
        _uiState.update { it.copy(groupReactions = reactions) }
    }

    fun publishFormulaGroup(formulaGroup: FormulaGroup) = viewModelScope.launch {
        _uiState.update { it.copy(isPublishInProgress = true) }
        formulasRepository.publishGroup(formulaGroup).fold(
            { _uiState.update { it.copy(isGroupPublished = false, isPublishInProgress = false) } },
            { _uiState.update { it.copy(isGroupPublished = true, isPublishInProgress = false) } }
        )
    }

    fun onFavouriteToggled(formulaGroup: FormulaGroup) = if (formulaGroup.id == -1) {
        val formulas = formulaStore.getLocal()
        formulaStore.setLocal(formulas.map { if (it == formulaGroup) it.copy(isFavourite = !it.isFavourite) else it })
    } else {
        val formulas = formulaStore.getRemote()
        formulaStore.setRemote(formulas.map { if (it == formulaGroup) it.copy(isFavourite = !it.isFavourite) else it })
    }

    fun onEditGroupClicked(formulaGroup: FormulaGroup) {
        navigator.navigate(EditGroupScreenDestination(formulaGroup).route)
    }

    fun onDeleteGroupClicked(formulaGroup: FormulaGroup) = if (formulaGroup.id == -1) {
        val formulas = formulaStore.getLocal()
        formulaStore.setLocal(formulas.mapNotNull { if (it == formulaGroup) null else it })
        navigator.goBack()
    } else {
        val formulas = formulaStore.getRemote()
        formulaStore.setRemote(formulas.mapNotNull { if (it == formulaGroup) null else it })
        navigator.goBack()
    }

    fun onFormulaClicked(formulaEntry: FormulaEntry) {
        val reactions =  _uiState.value.groupReactions?.filter { it.formulaId == formulaEntry.id }
        navigator.navigate(
            FormulaDetailsScreenDestination(
                formulaEntry,
                reactions?.count { it.type } ?: 0,
                reactions?.count { !it.type } ?: 0,
                reactions != null
            ).route
        )
    }
}