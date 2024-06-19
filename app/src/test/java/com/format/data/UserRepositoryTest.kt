package com.format.data

import arrow.core.Either
import com.format.common.model.AppError
import com.format.data.api.PublicApi
import com.format.data.api.LoginRequest
import com.format.data.api.LoginResponseDto
import com.format.data.api.RegisterResponseDto
import com.format.data.api.UserDto
import com.format.data.networking.util.parseError
import com.format.data.networking.token.TokenStore
import com.format.data.user.repository.UserRepositoryImpl
import com.format.domain.formulas.repository.FormulasRepository
import com.format.domain.formulas.store.FormulaStore
import com.format.domain.model.User
import com.format.domain.user.repository.UserRepository
import io.mockk.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.resetMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
class UserRepositoryTest {

    private lateinit var api: PublicApi
    private lateinit var ioDispatcher: CoroutineContext
    private lateinit var tokenStore: TokenStore
    private lateinit var formulasRepository: FormulasRepository
    private lateinit var formulaStore: FormulaStore
    private lateinit var userRepository: UserRepository

    @Before
    fun setup() {
        api = mockk()
        tokenStore = mockk(relaxed = true)
        formulasRepository = mockk(relaxed = true)
        formulaStore = mockk(relaxed = true)
        ioDispatcher = Dispatchers.Unconfined

        userRepository = UserRepositoryImpl(api, ioDispatcher as CoroutineDispatcher, tokenStore, formulasRepository, formulaStore)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login should return Unit on successful login`() = runBlockingTest {
        // Given
        val username = "user"
        val passwordHash = "pass"
        val loginResponseDto = mockk<LoginResponseDto>(relaxed = true)
        val response = Response.success(loginResponseDto)

        coEvery { api.login(any()) } returns response

        // When
        val result = userRepository.login(username, passwordHash)

        // Then
        assert(result.isRight())
        coVerify {
            api.login(LoginRequest(username, passwordHash))
            tokenStore.set(any(), any())
            formulasRepository.setDownloadedFormulas(any())
            formulasRepository.setUserReactions(any())
        }
    }

    @Test
    fun `login should return ApiError on failed login`() = runBlockingTest {
        // Given
        val username = "user"
        val passwordHash = "pass"
        val errorMessage = "Api Error"
        val response = Response.error<LoginResponseDto>(400, mockk(relaxed = true))

        coEvery { api.login(any()) } returns response
        coEvery { response.parseError() } returns Exception(errorMessage)

        // When
        val result = userRepository.login(username, passwordHash)

        // Then
        assert(result.isLeft())
        assert((result as Either.Left<AppError>).value.message == errorMessage)
    }

    @Test
    fun `register should return User on successful registration`() = runBlockingTest {
        // Given
        val username = "user"
        val passwordHash = "pass"
        val userDto = UserDto(id = 1, username = username, passwordHash = passwordHash)
        val registerResponseDto = RegisterResponseDto(user = userDto, message = "Success")
        val response = Response.success(registerResponseDto)

        coEvery { api.register(any()) } returns response

        // When
        val result = userRepository.register(username, passwordHash)

        // Then
        assert(result.isRight())
        val user = (result as Either.Right<User>).value
        assert(user.id == userDto.id)
        assert(user.username == userDto.username)
        assert(user.passwordHash == userDto.passwordHash)
    }

    @Test
    fun `register should return ApiError on failed registration`() = runBlockingTest {
        // Given
        val username = "user"
        val passwordHash = "pass"
        val errorMessage = "Api Error"
        val response = Response.error<RegisterResponseDto>(400, mockk(relaxed = true))

        coEvery { api.register(any()) } returns response
        coEvery { response.parseError() } returns Exception(errorMessage)

        // When
        val result = userRepository.register(username, passwordHash)

        // Then
        assert(result.isLeft())
        assert((result as Either.Left<AppError>).value.message == errorMessage)
    }
}
