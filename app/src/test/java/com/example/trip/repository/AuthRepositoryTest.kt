package com.example.trip.repository

import com.example.trip.Constants
import com.example.trip.dto.LoginRequestDto
import com.example.trip.dto.RegisterRequestDto
import com.example.trip.dto.UserDto
import com.example.trip.repositories.AuthRepository
import com.example.trip.service.AuthService
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class AuthRepositoryTest {

    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    private lateinit var repository: AuthRepository
    private val authService: AuthService = mockk()

    @Before
    fun setUp() {
        repository = AuthRepository(authService)
    }

    @Test
    fun `verify login correctly posted with header`() = scope.runTest {
        //given
        val value = mockkClass(UserDto::class)
        val response = mockkClass(Response::class)
        every { response.isSuccessful } returns true
        every { response.body() } returns value
        every { response.headers()[Constants.AUTHORIZATION_HEADER] } returns ""
        coEvery { authService.postLogin(any()) } returns response as Response<UserDto>

        //when
        val result = repository.login(mockkClass(LoginRequestDto::class))
        runCurrent()

        //then
        assertEquals(Pair(value, ""), result)
    }

    @Test(expected = HttpException::class)
    fun `verify login correctly posted without header`() = scope.runTest {
        //given
        val value = mockkClass(UserDto::class)
        val response = Response.success(value)
        coEvery { authService.postLogin(any()) } returns response


        //when
        repository.login(mockkClass(LoginRequestDto::class))
        runCurrent()
    }

    @Test(expected = HttpException::class)
    fun `verify login posted error`() = scope.runTest {
        //given
        val response = mockkClass(Response::class)
        every { response.isSuccessful } returns false
        every { response.code() } returns 404
        every { response.body() } returns "".toResponseBody()
        every { response.message() } returns ""
        every { response.headers()[Constants.AUTHORIZATION_HEADER] } returns ""
        coEvery { authService.postLogin(any()) } returns response as Response<UserDto>

        //when
        repository.login(mockkClass(LoginRequestDto::class))
        runCurrent()
    }

    @Test
    fun `verify register correctly posted`() = scope.runTest {
        //given
        val value = mockkClass(Void::class)
        val response = Response.success(value)
        coEvery { authService.postRegister(any()) } returns response

        //when
        repository.register(mockkClass(RegisterRequestDto::class))
        runCurrent()

        //then
        coVerify(exactly = 1) { authService.postRegister(any()) }
    }

    @Test(expected = HttpException::class)
    fun `verify register posted error`() = scope.runTest {
        //given
        val response = Response.error<Void>(404, "".toResponseBody())
        coEvery { authService.postRegister(any()) } returns response

        //when
        repository.register(mockkClass(RegisterRequestDto::class))
        runCurrent()
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

}