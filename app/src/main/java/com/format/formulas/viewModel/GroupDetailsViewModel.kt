package com.format.formulas.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import arrow.core.getOrElse
import com.format.app.navigation.navigator.Navigator
import com.format.destinations.EditGroupScreenDestination
import com.format.destinations.FormulaDetailsScreenDestination
import com.format.domain.formulas.repository.FormulasRepository
import com.format.domain.formulas.store.FormulaStore
import com.format.domain.model.FormulaEntry
import com.format.domain.model.FormulaGroup
import com.format.formulas.viewState.GroupDetailsViewState
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
        val publishedGroups = formulasRepository.groups().getOrElse { emptyList() }
        val reactions = formulasRepository.getReactions(formulaGroup).getOrElse { null }
        _uiState.update {
            it.copy(
                groupReactions = reactions,
                isGroupPublished = publishedGroups.firstOrNull { it.id == formulaGroup.id } != null,
            )
        }
    }

    fun publishFormulaGroup(formulaGroup: FormulaGroup) = viewModelScope.launch {
        _uiState.update { it.copy(isPublishInProgress = true) }
        formulasRepository.publishGroup(formulaGroup).fold(
            {
                _uiState.update { it.copy(isGroupPublished = false, isPublishInProgress = false) }
            },
            {
                val localGroups = formulaStore.getLocal()
                val filteredGroups = localGroups.filter { it != formulaGroup }
                val updatedGroups = filteredGroups + it
                formulaStore.setLocal(updatedGroups)
                _uiState.update { it.copy(isGroupPublished = true, isPublishInProgress = false) }
            }
        )
    }

    fun onFavouriteToggled(formulaGroup: FormulaGroup) {
        return if (formulaGroup.id == -1) {
            val formulas = formulaStore.getLocal()
            formulaStore.setLocal(formulas.map { if (it == formulaGroup) it.copy(isFavourite = !it.isFavourite) else it })
        } else {
            val formulaGroups = formulasRepository.downloadedFormulas()
            val targetFormulaId = formulaGroups.firstOrNull { it.id == formulaGroup.id }?.id ?: return
            val oldFavourites = formulaStore.getRemoteFavourite()
            if (oldFavourites.contains(targetFormulaId)) {
                formulaStore.setRemoteFavourite(oldFavourites.filter { it != targetFormulaId })
            } else {
                formulaStore.setRemoteFavourite(oldFavourites + targetFormulaId)
            }
        }
    }

    fun onEditGroupClicked(formulaGroup: FormulaGroup) {
        navigator.navigate(EditGroupScreenDestination(formulaGroup).route)
    }

    fun onDeleteGroupClicked(formulaGroup: FormulaGroup) = viewModelScope.launch {
        if (formulaGroup.id == -1) {
            val formulas = formulaStore.getLocal()
            formulaStore.setLocal(formulas.mapNotNull { if (it == formulaGroup) null else it })
            navigator.goBack()
        } else {
            val formulaGroups = formulasRepository.downloadedFormulas()
            val updatedFormula = formulaGroups.firstOrNull { it == formulaGroup } ?: return@launch
            formulasRepository.deleteRemoteGroup(updatedFormula.id).mapLeft { return@launch }
            formulasRepository.deleteDownloadedFormulas(updatedFormula)
            navigator.goBack()
        }
    }

    fun onFormulaClicked(formulaEntry: FormulaEntry) {
        val reactions = _uiState.value.groupReactions?.filter { it.formulaId == formulaEntry.id }
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