package com.format.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import arrow.core.right
import com.format.app.navigation.navigator.Navigator
import com.format.common.infrastructure.analytics.AnalyticsService
import com.format.common.infrastructure.logger.Logger
import com.format.common.model.AnalyticsScreen
import com.format.data.networking.token.TokenStore
import com.format.destinations.DownloadFormulaScreenDestination
import com.format.destinations.EditGroupScreenDestination
import com.format.destinations.GroupDetailsScreenDestination
import com.format.destinations.WelcomeScreenDestination
import com.format.domain.formulas.repository.FormulasRepository
import com.format.domain.formulas.store.FormulaStore
import com.format.domain.model.ApplicationFlows
import com.format.domain.model.FormulaGroup
import com.format.home.viewModel.HomeViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class HomeViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var navigator: Navigator
    private lateinit var formulaStore: FormulaStore
    private lateinit var tokenStore: TokenStore
    private lateinit var formulasRepository: FormulasRepository
    private lateinit var analyticsService: AnalyticsService
    private lateinit var logger: Logger

    private lateinit var viewModel: HomeViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        navigator = mockk(relaxed = true)
        formulaStore = mockk()
        tokenStore = mockk()
        formulasRepository = mockk()
        analyticsService = mockk(relaxed = true)
        logger = mockk(relaxed = true)

        viewModel = HomeViewModel(navigator, formulaStore, tokenStore, formulasRepository, analyticsService, logger)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadData should update uiState with formulas and track home screen`() = runTest {
        // Given
        val localFormulas = listOf(FormulaGroup.Empty.copy(id = 1), FormulaGroup.Empty.copy(id = 2))
        val remoteFormulas = listOf(FormulaGroup.Empty.copy(id = 3))
        val remoteFavourites = listOf(3)

        coEvery { formulaStore.getLocal() } returns localFormulas
        coEvery { formulaStore.getRemoteFavourite() } returns remoteFavourites
        coEvery { formulasRepository.downloadedFormulas() } returns remoteFormulas
        coEvery { formulasRepository.updateUserData() } returns Unit.right()

        // When
        viewModel.loadData()
        advanceUntilIdle()

        // Then
        val expectedRemoteFormulas = remoteFormulas.map { if (remoteFavourites.contains(it.id)) it.copy(isFavourite = true) else it }
        val expectedFavouriteFormulas = localFormulas.filter { it.isFavourite } + expectedRemoteFormulas.filter { it.isFavourite }

        assert(viewModel.uiState.value?.favouriteFormulas == expectedFavouriteFormulas)
        assert(viewModel.uiState.value?.localFormulas == localFormulas)
        assert(viewModel.uiState.value?.remoteFormulas == expectedRemoteFormulas)

        verify {
            analyticsService.trackScreen(AnalyticsScreen.HomeScreen)
            logger.i(ApplicationFlows.Home, "Home data loading started")
            logger.i(ApplicationFlows.Home, "Home data finished loading")
        }
    }

    @Test
    fun `onAddFormulasClicked should navigate to EditGroupScreen`() {
        // When
        viewModel.onAddFormulasClicked()

        // Then
        verify {
            navigator.navigate(EditGroupScreenDestination.route)
        }
    }

    @Test
    fun `onDownloadFormulasClicked should navigate to DownloadFormulaScreen`() {
        // When
        viewModel.onDownloadFormulasClicked()

        // Then
        verify {
            navigator.navigate(DownloadFormulaScreenDestination.route)
        }
    }

    @Test
    fun `onFormulaGroupClicked should navigate to GroupDetailsScreen with correct formula group`() {
        // Given
        val formulaGroup = FormulaGroup.Empty.copy(id = 1, name = "Group 1")

        // When
        viewModel.onFormulaGroupClicked(formulaGroup)

        // Then
        verify {
            navigator.navigate(GroupDetailsScreenDestination(formulaGroup).route)
        }
    }

    @Test
    fun `onLogoutClicked should clear data and navigate to WelcomeScreen`() = runTest {
        // Given
        coEvery { tokenStore.drop() } returns Unit
        coEvery { formulaStore.resetKey() } returns Unit
        coEvery { formulasRepository.setDownloadedFormulas(emptyList()) } returns Unit
        coEvery { formulasRepository.setUserReactions(emptyList()) } returns Unit

        // When
        viewModel.onLogoutClicked()
        advanceUntilIdle()

        // Then
        verify {
            logger.i(ApplicationFlows.Home, "User logout started")
            tokenStore.drop()
            formulaStore.resetKey()
            formulasRepository.setDownloadedFormulas(emptyList())
            formulasRepository.setUserReactions(emptyList())
            logger.i(ApplicationFlows.Home, "User logout finished")
            navigator.navigate(WelcomeScreenDestination.route, true)
        }
    }
}
