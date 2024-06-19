package com.format.formulas

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
import com.format.destinations.EditGroupScreenDestination
import com.format.destinations.FormulaDetailsScreenDestination
import com.format.domain.formulas.repository.FormulasRepository
import com.format.domain.formulas.store.FormulaStore
import com.format.domain.model.ApplicationFlows
import com.format.domain.model.FormulaEntry
import com.format.domain.model.FormulaGroup
import com.format.domain.model.Reaction
import com.format.formulas.viewModel.GroupDetailsViewModel
import com.format.formulas.viewState.GroupDetailsViewState
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class GroupDetailsViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var formulaStore: FormulaStore
    private lateinit var formulasRepository: FormulasRepository
    private lateinit var navigator: Navigator
    private lateinit var analyticsService: AnalyticsService
    private lateinit var logger: Logger

    private lateinit var viewModel: GroupDetailsViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        formulaStore = mockk()
        formulasRepository = mockk()
        navigator = mockk(relaxed = true)
        analyticsService = mockk(relaxed = true)
        logger = mockk(relaxed = true)

        viewModel = GroupDetailsViewModel(formulaStore, formulasRepository, navigator, analyticsService, logger)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadReactions should update uiState with reactions and published status`() = runTest {
        // Given
        val formulaGroup = FormulaGroup.Empty.copy(id = 1, name = "Group 1")
        val reactions = listOf<Reaction>()
        val publishedGroups = listOf(formulaGroup)

        coEvery { formulasRepository.groups() } returns publishedGroups.right()
        coEvery { formulasRepository.getReactions(formulaGroup) } returns reactions.right()

        val observer = mockk<Observer<GroupDetailsViewState>>(relaxed = true)
        viewModel.uiState.observeForever(observer)

        // When
        viewModel.loadReactions(formulaGroup)
        advanceUntilIdle()

        // Then
        verifySequence {
            observer.onChanged(match { it.groupReactions == null && it.isGroupPublished == false })
            observer.onChanged(match { it.groupReactions == reactions && it.isGroupPublished == true })
        }

        verify {
            analyticsService.trackScreen(AnalyticsScreen.GroupPreviewScreen)
            logger.i(ApplicationFlows.Formula, "Group data loading started")
            logger.i(ApplicationFlows.Formula, "Group data loading finished")
        }

        viewModel.uiState.removeObserver(observer)
    }

    @Test
    fun `publishFormulaGroup should update uiState correctly on success`() = runTest {
        // Given
        val formulaGroup = FormulaGroup.Empty.copy(id = 1, name = "Group 1")
        val updatedGroup = formulaGroup.copy(name = "Updated Group")

        coEvery { formulasRepository.publishGroup(formulaGroup) } returns updatedGroup.right()
        coEvery { formulaStore.getLocal() } returns listOf(formulaGroup)
        coEvery { formulaStore.setLocal(any()) } just Runs

        val observer = mockk<Observer<GroupDetailsViewState>>(relaxed = true)
        viewModel.uiState.observeForever(observer)

        // When
        viewModel.publishFormulaGroup(formulaGroup)
        advanceUntilIdle()

        // Then
        verify {
            logger.i(ApplicationFlows.Formula, "Group publishing started")
            logger.i(ApplicationFlows.Formula, "Group publishing finished")
            assert(viewModel.uiState.value?.isGroupPublished == true)
            assert(viewModel.uiState.value?.isPublishInProgress == false)
        }

        viewModel.uiState.removeObserver(observer)
    }

    @Test
    fun `publishFormulaGroup should update uiState correctly on failure`() = runTest {
        // Given
        val formulaGroup = FormulaGroup.Empty.copy(id = 1, name = "Group 1")
        val errorMessage = "Publish failed"

        coEvery { formulasRepository.publishGroup(formulaGroup) } returns AppError.ApiError(errorMessage).left()

        val observer = mockk<Observer<GroupDetailsViewState>>(relaxed = true)
        viewModel.uiState.observeForever(observer)

        // When
        viewModel.publishFormulaGroup(formulaGroup)
        advanceUntilIdle()

        // Then
        verify {
            logger.i(ApplicationFlows.Formula, "Group publishing started")
            logger.w(ApplicationFlows.Formula, "Group publishing failed with message: $errorMessage")
            assert(viewModel.uiState.value?.isGroupPublished == false)
            assert(viewModel.uiState.value?.isPublishInProgress == false)
        }

        viewModel.uiState.removeObserver(observer)
    }

    @Test
    fun `onFavouriteToggled should update favourite status locally`() {
        // Given
        val formulaGroup = FormulaGroup.Empty.copy(id = -1, name = "Group 1", isFavourite = false, isLocal = true)
        val localGroups = listOf(formulaGroup)

        every { formulaStore.getLocal() } returns localGroups
        every { formulaStore.setLocal(any()) } just Runs

        // When
        viewModel.onFavouriteToggled(formulaGroup)

        // Then
        verify {
            formulaStore.setLocal(localGroups.map { it.copy(isFavourite = true) })
            logger.i(ApplicationFlows.Formula, "Local group favorite toggled")
        }
    }

    @Test
    fun `onFavouriteToggled should update favourite status remotely`() {
        // Given
        val formulaGroup = FormulaGroup.Empty.copy(id = 1, name = "Group 1", isFavourite = false, isLocal = false)
        val remoteGroups = listOf(formulaGroup)
        val oldFavourites = listOf<Int>()

        every { formulaStore.getRemoteFavourite() } returns oldFavourites
        every { formulaStore.setRemoteFavourite(any()) } just Runs
        every { formulasRepository.downloadedFormulas() } returns remoteGroups

        // When
        viewModel.onFavouriteToggled(formulaGroup)

        // Then
        verify {
            formulaStore.setRemoteFavourite(oldFavourites + formulaGroup.id)
            analyticsService.trackEvent(AnalyticsEvent.FavouriteToggled(formulaGroup.id, true))
            logger.i(ApplicationFlows.Formula, "Remote group favorite toggled")
        }
    }

    @Test
    fun `onEditGroupClicked should navigate to EditGroupScreen`() {
        // Given
        val formulaGroup = FormulaGroup.Empty.copy(id = 1, name = "Group 1")

        // When
        viewModel.onEditGroupClicked(formulaGroup)

        // Then
        verify {
            navigator.navigate(EditGroupScreenDestination(formulaGroup).route)
        }
    }

    @Test
    fun `onDeleteGroupClicked should delete local group and navigate back`() = runTest {
        // Given
        val formulaGroup = FormulaGroup.Empty.copy(id = -1, name = "Group 1", isLocal = true)
        val localGroups = listOf(formulaGroup)

        every { formulaStore.getLocal() } returns localGroups
        every { formulaStore.setLocal(any()) } just Runs
        every { navigator.goBack() } just Runs

        // When
        viewModel.onDeleteGroupClicked(formulaGroup)
        advanceUntilIdle()

        // Then
        verify {
            formulaStore.setLocal(emptyList())
            logger.i(ApplicationFlows.Formula, "Local group removed")
            navigator.goBack()
        }
    }

    @Test
    fun `onDeleteGroupClicked should delete remote group and navigate back`() = runTest {
        // Given
        val formulaGroup = FormulaGroup.Empty.copy(id = 1, name = "Group 1", isLocal = false)
        val remoteGroups = listOf(formulaGroup)

        coEvery { formulasRepository.downloadedFormulas() } returns remoteGroups
        coEvery { formulasRepository.deleteRemoteGroup(any()) } returns Unit.right()
        coEvery { formulasRepository.deleteDownloadedFormulas(any()) } just Runs
        every { analyticsService.trackEvent(any()) } just Runs
        every { navigator.goBack() } just Runs

        // When
        viewModel.onDeleteGroupClicked(formulaGroup)
        advanceUntilIdle()

        // Then
        coVerify {
            formulasRepository.deleteRemoteGroup(formulaGroup.id)
            formulasRepository.deleteDownloadedFormulas(formulaGroup)
            analyticsService.trackEvent(AnalyticsEvent.FormulaDeleted(formulaGroup.id))
            logger.i(ApplicationFlows.Formula, "Remote group removed")
            navigator.goBack()
        }
    }

    @Test
    fun `onFormulaClicked should navigate to FormulaDetailsScreen`() {
        // Given
        val formulaEntry = FormulaEntry(id = 1, title = "Formula 1")
        val reactions = listOf<Reaction>()

//        viewModel.uiState.value = GroupDetailsViewState(groupReactions = reactions)

        // When
        viewModel.onFormulaClicked(formulaEntry)

        // Then
        verify {
            navigator.navigate(
                FormulaDetailsScreenDestination(
                    formulaEntry,
                    reactions.count { it.type },
                    reactions.count { !it.type },
                    reactions.isNotEmpty()
                ).route
            )
        }
    }
}
