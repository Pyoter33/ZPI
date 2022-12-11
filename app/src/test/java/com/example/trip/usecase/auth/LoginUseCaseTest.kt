package com.example.trip.usecase.auth

import com.example.trip.dto.LoginRequestDto
import com.example.trip.dto.UserDto
import com.example.trip.models.Resource
import com.example.trip.repositories.AuthRepository
import com.example.trip.usecases.auth.LoginUseCase
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.mockkClass
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class LoginUseCaseTest {

    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    private lateinit var useCase: LoginUseCase
    private val authRepository: AuthRepository = mockk()
    private val loginRequestDto = mockkClass(LoginRequestDto::class)
    private val user = mockkClass(UserDto::class)

    @Before
    fun setUp() {
        useCase = LoginUseCase(authRepository)
    }

    @Test
    fun `verify login correctly acquired`() = scope.runTest {
        //given
        coEvery { authRepository.login(any()) } returns Pair(user, "")

        //when
        val result = useCase(loginRequestDto)
        runCurrent()

        //then
        assert(result is Resource.Success)
    }

    @Test
    fun `verify result failure Http`() = scope.runTest {
        //given
        coEvery { authRepository.login(any()) } throws HttpException(Response.error<List<*>>(403, "".toResponseBody()))

        //when
        val result = useCase(loginRequestDto)
        runCurrent()

        //then
        assertEquals(Resource.Failure<List<*>>(403), result)
    }

    @Test
    fun `verify result failure other`() = scope.runTest {
        //given
        coEvery { authRepository.login(any()) } throws Exception()

        //when
        val result = useCase(loginRequestDto)
        runCurrent()

        //then
        assertEquals(Resource.Failure<List<*>>(0), result)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }


}