package com.format.formulas

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import arrow.core.right
import com.format.common.infrastructure.analytics.AnalyticsService
import com.format.common.infrastructure.logger.Logger
import com.format.common.model.AnalyticsScreen
import com.format.common.model.Epoch
import com.format.common.model.plusHours
import com.format.data.infrastructure.dateTime.DateTimeProvider
import com.format.data.networking.token.Token
import com.format.data.networking.token.TokenStore
import com.format.data.networking.token.Tokens
import com.format.domain.formulas.repository.FormulasRepository
import com.format.domain.model.ApplicationFlows
import com.format.domain.model.FormulaEntry
import com.format.domain.model.Reaction
import com.format.formulas.viewModel.FormulaDetailsViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class FormulaDetailsViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var tokenStore: TokenStore
    private lateinit var dateTimeProvider: DateTimeProvider
    private lateinit var formulasRepository: FormulasRepository
    private lateinit var analyticsService: AnalyticsService
    private lateinit var logger: Logger

    private lateinit var viewModel: FormulaDetailsViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        tokenStore = mockk()
        dateTimeProvider = mockk()
        formulasRepository = mockk()
        analyticsService = mockk(relaxed = true)
        logger = mockk(relaxed = true)

        // Mocking token and dateTimeProvider behavior
        val now = Epoch(System.currentTimeMillis())
        val validToken = mockk<Token> {
            every { expiresAt } returns now.plusHours(1)
        }
        every { tokenStore.get() } returns Tokens(validToken, validToken)
        every { dateTimeProvider.now() } returns now

        viewModel = FormulaDetailsViewModel(tokenStore, dateTimeProvider, formulasRepository, analyticsService, logger)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadData should update state with reaction`() = runTest {
        // Given
        val formulaId = 1
        val userReaction = Reaction(true, Epoch.None, formulaId)
        coEvery { formulasRepository.userReactions() } returns listOf(userReaction)

        // When
        viewModel.loadData(formulaId)
        advanceUntilIdle()

        // Then
        coVerify {
            analyticsService.trackScreen(AnalyticsScreen.FormulaPreviewScreen)
            logger.i(ApplicationFlows.Formula, "Formula data loading started")
            logger.i(ApplicationFlows.Formula, "Formula data loading finished")
        }
    }

    @Test
    fun `onSuccessClicked should update state and log event`() = runTest {
        // Given
        val formulaEntry = FormulaEntry(id = 1, title = "Test", formula = "Test content")
        coEvery { formulasRepository.react(formulaEntry, true) } returns Unit.right()

        // When
        viewModel.onSuccessClicked(formulaEntry)
        advanceUntilIdle()

        // Then
        coVerify {
            formulasRepository.react(formulaEntry, true)
            logger.i(ApplicationFlows.Formula, "User reacted positively on formula ${formulaEntry.id}")
        }
    }

    @Test
    fun `onFailureClicked should update state and log event`() = runTest {
        // Given
        val formulaEntry = FormulaEntry(id = 1, title = "Test", formula = "Test content")
        coEvery { formulasRepository.react(formulaEntry, false) } returns Unit.right()

        // When
        viewModel.onFailureClicked(formulaEntry)
        advanceUntilIdle()

        // Then
        coVerify {
            formulasRepository.react(formulaEntry, false)
            logger.i(ApplicationFlows.Formula, "User reacted positively on formula ${formulaEntry.id}")
        }
    }
}
