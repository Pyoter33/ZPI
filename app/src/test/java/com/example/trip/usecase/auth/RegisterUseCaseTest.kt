package com.example.trip.usecase.auth

import com.example.trip.dto.RegisterRequestDto
import com.example.trip.models.Resource
import com.example.trip.repositories.AuthRepository
import com.example.trip.usecases.auth.RegisterUseCase
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
class RegisterUseCaseTest {

    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    private lateinit var useCase: RegisterUseCase
    private val authRepository: AuthRepository = mockk()
    private val registerRequestDto = mockkClass(RegisterRequestDto::class)

    @Before
    fun setUp() {
        useCase = RegisterUseCase(authRepository)
    }

    @Test
    fun `verify correctly registered`() = scope.runTest {
        //given
        coEvery { authRepository.register(any()) } returns Unit

        //when
        val result = useCase(registerRequestDto)
        runCurrent()

        //then
        assert(result is Resource.Success)
    }

    @Test
    fun `verify result failure Http`() = scope.runTest {
        //given
        val error = """{"message":"error", "status": BAD_REQUEST, "timestamp": "..."}"""
        coEvery { authRepository.register(any()) } throws HttpException(Response.error<List<*>>(403, error.toResponseBody()))

        //when
        val result = useCase(registerRequestDto)
        runCurrent()

        //then
        assertEquals(Resource.Failure<List<*>>(403), result)
    }

    @Test
    fun `verify result failure other`() = scope.runTest {
        //given
        coEvery { authRepository.register(any()) } throws Exception()

        //when
        val result = useCase(registerRequestDto)
        runCurrent()

        //then
        assertEquals(Resource.Failure<List<*>>(0), result)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }


}