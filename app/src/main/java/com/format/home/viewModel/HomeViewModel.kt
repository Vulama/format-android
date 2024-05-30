package com.format.home.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.format.app.navigation.navigator.Navigator
import com.format.data.networking.token.TokenStore
import com.format.destinations.DownloadFormulaScreenDestination
import com.format.destinations.EditGroupScreenDestination
import com.format.destinations.GroupDetailsScreenDestination
import com.format.destinations.WelcomeScreenDestination
import com.format.domain.formulas.repository.FormulasRepository
import com.format.domain.formulas.store.FormulaStore
import com.format.domain.model.FormulaGroup
import com.format.home.viewState.HomeViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val navigator: Navigator,
    private val formulaStore: FormulaStore,
    private val tokenStore: TokenStore,
    private val formulasRepository: FormulasRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeViewState())
    val uiState: LiveData<HomeViewState>
        get() = _uiState.asLiveData()

    fun loadData() = viewModelScope.launch {
        formulasRepository.updateUserData()
        val remoteFavourites = formulaStore.getRemoteFavourite()
        val localFormulas = formulaStore.getLocal()
        val remoteFormulas = formulasRepository.downloadedFormulas().map {
            if (remoteFavourites.contains(it.id)) it.copy(isFavourite = true) else it
        }

        _uiState.update {
            it.copy(
                favouriteFormulas = localFormulas.filter { it.isFavourite } + remoteFormulas.filter { it.isFavourite },
                localFormulas = localFormulas,
                remoteFormulas = remoteFormulas,
            )
        }
    }

    fun onAddFormulasClicked() {
        navigator.navigate(EditGroupScreenDestination.route)
    }

    fun onDownloadFormulasClicked() {
        navigator.navigate(DownloadFormulaScreenDestination.route)
    }

    fun onFormulaGroupClicked(formulaGroup: FormulaGroup) {
        navigator.navigate(GroupDetailsScreenDestination(formulaGroup).route)
    }

    fun onLogoutClicked() {
        tokenStore.drop()
        formulaStore.resetKey()
        formulasRepository.setDownloadedFormulas(emptyList())
        formulasRepository.setUserReactions(emptyList())
        navigator.navigate(WelcomeScreenDestination.route, true)
    }
}