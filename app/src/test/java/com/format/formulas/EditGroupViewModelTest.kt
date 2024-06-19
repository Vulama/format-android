package com.format.formulas

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.format.app.navigation.navigator.Navigator
import com.format.common.infrastructure.analytics.AnalyticsService
import com.format.common.infrastructure.logger.Logger
import com.format.common.model.AnalyticsScreen
import com.format.domain.formulas.store.FormulaStore
import com.format.domain.model.ApplicationFlows
import com.format.domain.model.FormulaEntry
import com.format.domain.model.FormulaGroup
import com.format.formulas.viewModel.EditGroupViewModel
import io.mockk.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.net.URLEncoder

class EditGroupViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var formulaStore: FormulaStore
    private lateinit var navigator: Navigator
    private lateinit var analyticsService: AnalyticsService
    private lateinit var logger: Logger
    private lateinit var viewModel: EditGroupViewModel

    @Before
    fun setup() {
        formulaStore = mockk(relaxed = true)
        navigator = mockk(relaxed = true)
        analyticsService = mockk(relaxed = true)
        logger = mockk(relaxed = true)

        viewModel = EditGroupViewModel(formulaStore, navigator, analyticsService, logger)
    }

    @Test
    fun `init should track GroupEditScreen screen`() {
        verify { analyticsService.trackScreen(AnalyticsScreen.GroupEditScreen) }
    }

    @Test
    fun `addLocalGroup should add group and navigate back`() {
        // Given
        val groupName = "Test Group"
        val formulas = listOf(
            FormulaEntry(id = 1, formula = "H2O", title = "Water"),
            FormulaEntry(id = 2, formula = "CO2", title = "Carbon Dioxide")
        )
        val encodedFormulas = formulas.map {
            it.copy(formula = URLEncoder.encode(it.formula, "UTF-8"))
        }

        every { formulaStore.getLocal() } returns listOf()

        // When
        viewModel.addLocalGroup(groupName, formulas)

        // Then
        verify {
            logger.i(ApplicationFlows.Formula, "Starting to add local group")
            formulaStore.setLocal(withArg {
                assert(it.size == 1)
                assert(it[0].name == groupName)
                assert(it[0].formulas == encodedFormulas)
            })
            logger.i(ApplicationFlows.Formula, "Local group added")
            navigator.goBack()
        }
    }

    @Test
    fun `updateGroup should update group and navigate to GroupDetailsScreen`() {
        // Given
        val groupName = "Updated Group"
        val formulas = listOf(
            FormulaEntry(id = 1, formula = "NaCl", title = "Salt"),
            FormulaEntry(id = 2, formula = "C6H12O6", title = "Glucose")
        )
        val oldFormulaGroup = FormulaGroup(id = 1, name = "Old Group", formulas = emptyList(), isFavourite = true)
        val encodedFormulas = formulas.map {
            it.copy(formula = URLEncoder.encode(it.formula, "UTF-8"))
        }

        every { formulaStore.getLocal() } returns listOf(oldFormulaGroup)

        // When
        viewModel.updateGroup(groupName, formulas, oldFormulaGroup)

        // Then
        verify {
            logger.i(ApplicationFlows.Formula, "Updating group stared")
            formulaStore.setLocal(withArg {
                assert(it.size == 1)
                assert(it[0].name == groupName)
                assert(it[0].formulas == encodedFormulas)
                assert(it[0].isFavourite)
            })
            logger.i(ApplicationFlows.Formula, "Updating group finished")
            navigator.goBack()
            navigator.goBack()
            navigator.navigate(any())
        }
    }
}
