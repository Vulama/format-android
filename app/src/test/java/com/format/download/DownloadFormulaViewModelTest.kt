package com.format.download

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import arrow.core.left
import arrow.core.right
import com.format.app.navigation.navigator.Navigator
import com.format.common.infrastructure.analytics.AnalyticsService
import com.format.common.infrastructure.logger.Logger
import com.format.common.model.AnalyticsEvent
import com.format.common.model.AnalyticsScreen
import com.format.common.model.AppError
import com.format.domain.formulas.repository.FormulasRepository
import com.format.domain.model.ApplicationFlows
import com.format.domain.model.FormulaGroup
import com.format.download.viewModel.DownloadFormulaViewModel
import com.format.download.viewState.DownloadFormulaViewState
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class DownloadFormulaViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var formulasRepository: FormulasRepository
    private lateinit var navigator: Navigator
    private lateinit var analyticsService: AnalyticsService
    private lateinit var logger: Logger
    private lateinit var viewModel: DownloadFormulaViewModel

    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        formulasRepository = mockk()
        navigator = mockk(relaxed = true)
        analyticsService = mockk(relaxed = true)
        logger = mockk(relaxed = true)

        viewModel = DownloadFormulaViewModel(formulasRepository, navigator, analyticsService, logger)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `loadData should load remote formula groups and update uiState`() = runBlockingTest {
        // Given
        val remoteFormulaGroups = listOf(
            FormulaGroup.Empty.copy(id = 1, name = "Group 1"),
            FormulaGroup.Empty.copy(id = 2, name = "Group 2")
        )

        coEvery { formulasRepository.groups() } returns remoteFormulaGroups.right()

        val observer = mockk<Observer<DownloadFormulaViewState>>(relaxed = true)
        viewModel.uiState.observeForever(observer)

        // When
        viewModel.loadData()

        // Then
        verify {
            logger.i(ApplicationFlows.Download, "Loading data started")
            analyticsService.trackScreen(AnalyticsScreen.DownloadScreen)
            logger.i(ApplicationFlows.Download, "Loading data finished")
            observer.onChanged(DownloadFormulaViewState(remoteFormulaGroups))
        }

        viewModel.uiState.removeObserver(observer)
    }

    @Test
    fun `onDownloadFormulaClicked should download formula group and navigate back`() = runBlockingTest {
        // Given
        val formulaGroup = FormulaGroup.Empty.copy(id = 1, name = "Group 1")

        coEvery { formulasRepository.downloadFormulaGroup(formulaGroup.id) } returns Unit.right()
        coEvery { formulasRepository.updateDownloadedFormulas(formulaGroup) } just Runs

        // When
        viewModel.onDownloadFormulaClicked(formulaGroup)

        // Then
        coVerifySequence {
            formulasRepository.downloadFormulaGroup(formulaGroup.id)
            formulasRepository.updateDownloadedFormulas(formulaGroup)
            analyticsService.trackEvent(AnalyticsEvent.FormulaDownloaded(formulaGroup.id))
            logger.i(ApplicationFlows.Download, "Group download is successful")
            navigator.goBack()
        }
    }

    @Test
    fun `onDownloadFormulaClicked should log error if download fails`() = runBlockingTest {
        // Given
        val formulaGroup = FormulaGroup.Empty.copy(id = 1, name = "Group 1")
        val errorMessage = "Network error"

        coEvery { formulasRepository.downloadFormulaGroup(formulaGroup.id) } returns AppError.ApiError(errorMessage).left()

        // When
        viewModel.onDownloadFormulaClicked(formulaGroup)

        // Then
        coVerifySequence {
            formulasRepository.downloadFormulaGroup(formulaGroup.id)
            logger.w(ApplicationFlows.Download, "Downloaded group failed to link to user, aborting download: $errorMessage")
        }

        coVerify(exactly = 0) {
            formulasRepository.updateDownloadedFormulas(any())
            analyticsService.trackEvent(any())
            logger.i(any(), any())
            navigator.goBack()
        }
    }
}
