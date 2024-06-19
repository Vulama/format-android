package com.format.data

import arrow.core.Either
import com.format.common.model.AppError
import com.format.data.api.*
import com.format.data.formulas.repository.FormulasRepositoryImpl
import com.format.domain.formulas.repository.FormulasRepository
import com.format.domain.model.FormulaEntry
import com.format.domain.model.FormulaGroup
import io.mockk.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.resetMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
class FormulasRepositoryTest {

    private lateinit var api: PublicApi
    private lateinit var restrictedApi: RestrictedApi
    private lateinit var ioDispatcher: CoroutineContext
    private lateinit var formulasRepository: FormulasRepository

    @Before
    fun setup() {
        api = mockk()
        restrictedApi = mockk()
        ioDispatcher = Dispatchers.Unconfined

        formulasRepository = FormulasRepositoryImpl(api, restrictedApi, ioDispatcher as CoroutineDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `groups should return list of FormulaGroups on success`() = runBlockingTest {
        // Given
        val groupDto = GroupDto(
            name = "Group 1",
            formulas = listOf(FormulaDto("Title", "Formula", "Description")),
            id = 1,
            ownerId = 1,
        )
        val response = listOf(groupDto)

        coEvery { api.groups() } returns response

        // When
        val result = formulasRepository.groups()

        // Then
        assert(result.isRight())
        val groups = (result as Either.Right).value
        assert(groups.isNotEmpty())
        assert(groups[0].name == groupDto.name)
        assert(groups[0].formulas[0].title == groupDto.formulas[0].title)
        coVerify { api.groups() }
    }

    @Test
    fun `groups should return ApiError on failure`() = runBlockingTest {
        // Given
        val errorMessage = "Error"
        coEvery { api.groups() } throws Exception(errorMessage)

        // When
        val result = formulasRepository.groups()

        // Then
        assert(result.isLeft())
        val error = (result as Either.Left).value
        assert(error is AppError.ApiError)
        assert((error as AppError.ApiError).message == errorMessage)
    }

    @Test
    fun `publishGroup should return FormulaGroup on success`() = runBlockingTest {
        // Given
        val formulaGroup = FormulaGroup("Group 1", listOf(FormulaEntry("Title", "Formula", "Description")), id = 1)
        val publishGroupDto = PublishGroupDto(
            name = formulaGroup.name,
            formulas = formulaGroup.formulas.map { FormulaDto(it.title, it.formula, it.description) }
        )
        val response = PublishGroupResponseDto(
            message = "Success",
            group = GroupDto(
                name = formulaGroup.name,
                formulas = formulaGroup.formulas.map { FormulaDto(it.title, it.formula, it.description, it.id) },
                id = 1,
                ownerId = 1,
            )
        )

        coEvery { restrictedApi.publishGroup(publishGroupDto) } returns response

        // When
        val result = formulasRepository.publishGroup(formulaGroup)

        // Then
        assert(result.isRight())
        val publishedGroup = (result as Either.Right).value
        assert(publishedGroup.id == formulaGroup.id)
        assert(publishedGroup.name == formulaGroup.name)
        coVerify { restrictedApi.publishGroup(publishGroupDto) }
    }

    @Test
    fun `publishGroup should return ApiError on failure`() = runBlockingTest {
        // Given
        val formulaGroup = FormulaGroup("Group 1", listOf(FormulaEntry("Title", "Formula", "Description")), id = 1)
        val errorMessage = "Error"
        coEvery { restrictedApi.publishGroup(any()) } throws Exception(errorMessage)

        // When
        val result = formulasRepository.publishGroup(formulaGroup)

        // Then
        assert(result.isLeft())
        val error = (result as Either.Left).value
        assert(error is AppError.ApiError)
        assert((error as AppError.ApiError).message == errorMessage)
    }

    @Test
    fun `getReactions should return list of Reactions on success`() = runBlockingTest {
        // Given
        val formulaGroup = FormulaGroup("Group 1", listOf(FormulaEntry("Title", "Formula", "Description")), id = 1)
        val reactionDto = ReactionDto("true", "2023-06-18T00:00:00Z", 1)
        val response = ReactionsResponseDto(listOf(reactionDto))

        coEvery { restrictedApi.reactions(any()) } returns response

        // When
        val result = formulasRepository.getReactions(formulaGroup)

        // Then
        assert(result.isRight())
        val reactions = (result as Either.Right).value
        assert(reactions.isNotEmpty())
        assert(reactions[0].type == true)
        coVerify { restrictedApi.reactions(ReactionsRequestDto(formulaGroup.id)) }
    }

    @Test
    fun `getReactions should return ApiError on failure`() = runBlockingTest {
        // Given
        val formulaGroup = FormulaGroup("Group 1", listOf(FormulaEntry("Title", "Formula", "Description")), id = 1)
        val errorMessage = "Error"
        coEvery { restrictedApi.reactions(any()) } throws Exception(errorMessage)

        // When
        val result = formulasRepository.getReactions(formulaGroup)

        // Then
        assert(result.isLeft())
        val error = (result as Either.Left).value
        assert(error is AppError.ApiError)
        assert((error as AppError.ApiError).message == errorMessage)
    }

    @Test
    fun `react should return Unit on success`() = runBlockingTest {
        // Given
        val formulaEntry = FormulaEntry("Title", "Formula", "Description", id = 1)
        val formulaReactDto = FormulaReactDto(formulaEntry.id, "true")
        val formulaReactResponseDto = FormulaReactResponseDto("Success", ReactionDto("true", "2023-06-18T00:00:00Z", 1))

        coEvery { restrictedApi.formulaReact(formulaReactDto) } returns formulaReactResponseDto

        // When
        val result = formulasRepository.react(formulaEntry, true)

        // Then
        assert(result.isRight())
        coVerify { restrictedApi.formulaReact(formulaReactDto) }
    }

    @Test
    fun `react should return ApiError on failure`() = runBlockingTest {
        // Given
        val formulaEntry = FormulaEntry("Title", "Formula", "Description", id = 1)
        val errorMessage = "Error"
        coEvery { restrictedApi.formulaReact(any()) } throws Exception(errorMessage)

        // When
        val result = formulasRepository.react(formulaEntry, true)

        // Then
        assert(result.isLeft())
        val error = (result as Either.Left).value
        assert(error is AppError.ApiError)
        assert((error as AppError.ApiError).message == errorMessage)
    }

    @Test
    fun `downloadFormulaGroup should return Unit on success`() = runBlockingTest {
        // Given
        val groupId = 1
        val remoteFormulaRequest = RemoteFormulaRequest(groupId)

        coEvery { restrictedApi.groupDownloaded(remoteFormulaRequest) } returns DownloadedGroupDto(1, 1)

        // When
        val result = formulasRepository.downloadFormulaGroup(groupId)

        // Then
        assert(result.isRight())
        coVerify { restrictedApi.groupDownloaded(remoteFormulaRequest) }
    }

    @Test
    fun `downloadFormulaGroup should return ApiError on failure`() = runBlockingTest {
        // Given
        val groupId = 1
        val errorMessage = "Error"
        coEvery { restrictedApi.groupDownloaded(any()) } throws Exception(errorMessage)

        // When
        val result = formulasRepository.downloadFormulaGroup(groupId)

        // Then
        assert(result.isLeft())
        val error = (result as Either.Left).value
        assert(error is AppError.ApiError)
        assert((error as AppError.ApiError).message == errorMessage)
    }

    @Test
    fun `deleteRemoteGroup should return Unit on success`() = runBlockingTest {
        // Given
        val groupId = 1
        val remoteFormulaRequest = RemoteFormulaRequest(groupId)

        coEvery { restrictedApi.groupDeleted(remoteFormulaRequest) } returns DownloadedGroupDto(1, 1)

        // When
        val result = formulasRepository.deleteRemoteGroup(groupId)

        // Then
        assert(result.isRight())
        coVerify { restrictedApi.groupDeleted(remoteFormulaRequest) }
    }

    @Test
    fun `deleteRemoteGroup should return ApiError on failure`() = runBlockingTest {
        // Given
        val groupId = 1
        val errorMessage = "Error"
        coEvery { restrictedApi.groupDeleted(any()) } throws Exception(errorMessage)

        // When
        val result = formulasRepository.deleteRemoteGroup(groupId)

        // Then
        assert(result.isLeft())
        val error = (result as Either.Left).value
        assert(error is AppError.ApiError)
        assert((error as AppError.ApiError).message == errorMessage)
    }

    @Test
    fun `updateUserData should return ApiError on failure`() = runBlockingTest {
        // Given
        val errorMessage = "Error"
        coEvery { restrictedApi.loadUserData() } throws Exception(errorMessage)

        // When
        val result = formulasRepository.updateUserData()

        // Then
        assert(result.isLeft())
        val error = (result as Either.Left).value
        assert(error is AppError.ApiError)
        assert((error as AppError.ApiError).message == errorMessage)
    }
}
