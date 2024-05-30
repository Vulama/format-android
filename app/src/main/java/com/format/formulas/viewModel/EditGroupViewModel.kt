package com.format.formulas.viewModel

import androidx.lifecycle.ViewModel
import com.format.app.navigation.navigator.Navigator
import com.format.common.infrastructure.analytics.AnalyticsService
import com.format.common.infrastructure.logger.Logger
import com.format.common.model.AnalyticsScreen
import com.format.destinations.GroupDetailsScreenDestination
import com.format.domain.formulas.store.FormulaStore
import com.format.domain.model.ApplicationFlows
import com.format.domain.model.FormulaEntry
import com.format.domain.model.FormulaGroup

class EditGroupViewModel(
    private val formulaStore: FormulaStore,
    private val navigator: Navigator,
    private val analyticsService: AnalyticsService,
    private val logger: Logger,
) : ViewModel() {
    init {
        analyticsService.trackScreen(AnalyticsScreen.GroupEditScreen)
    }

    fun addLocalGroup(groupName: String, formulas: List<FormulaEntry>) {
        logger.i(ApplicationFlows.Formula, "Starting to add local group")
        val localGroups = formulaStore.getLocal()
        formulaStore.setLocal(localGroups + FormulaGroup(groupName, formulas))
        logger.i(ApplicationFlows.Formula, "Local group added")
        navigator.goBack()
    }

    fun updateGroup(groupName: String, formulas: List<FormulaEntry>, oldFormulaGroup: FormulaGroup?) {
        logger.i(ApplicationFlows.Formula, "Updating group stared")
        val localGroups = formulaStore.getLocal()
        val updatedFormulaGroup = FormulaGroup(groupName, formulas, isFavourite = oldFormulaGroup?.isFavourite ?: false)
        val updatedGroups = localGroups.map { if (it == oldFormulaGroup) updatedFormulaGroup else it }
        formulaStore.setLocal(updatedGroups)
        logger.i(ApplicationFlows.Formula, "Updating group finished")
        navigator.goBack()
        navigator.goBack()
        navigator.navigate(GroupDetailsScreenDestination(updatedFormulaGroup).route)
    }
}