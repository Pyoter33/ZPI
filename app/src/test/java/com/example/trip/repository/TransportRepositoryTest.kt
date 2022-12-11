package com.example.trip.repository

import com.example.trip.dto.TransportDto
import com.example.trip.dto.UserTransportPostDto
import com.example.trip.repositories.TransportRepository
import com.example.trip.service.TransportService
import com.google.maps.GeoApiContext
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
class TransportRepositoryTest {

    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    private lateinit var repository: TransportRepository
    private val transportService: TransportService = mockk()
    private val geoApiContext: GeoApiContext = mockk()

    @Before
    fun setUp() {
        repository = TransportRepository(geoApiContext, transportService)
    }

    @Test
    fun `verify transport correctly acquired`() = scope.runTest {
        //given
        val value = listOf(mockkClass(TransportDto::class))
        val response = Response.success(value)
        coEvery { transportService.getTransport(any()) } returns response

        //when
        val result = repository.getTransport(1L)
        runCurrent()

        //then
        assertEquals(value, result)
    }

    @Test(expected = HttpException::class)
    fun `verify transport acquired error`() = scope.runTest {
        //given
        val response = Response.error<List<TransportDto>>(404, "".toResponseBody())
        coEvery { transportService.getTransport(any()) } returns response

        //when
        repository.getTransport(1L)
        runCurrent()
    }

    @Test
    fun `verify transport correctly posted`() = scope.runTest {
        //given
        val response = Response.success(mockkClass(Void::class))
        coEvery { transportService.postUserTransport(any(), any()) } returns response

        //when
        repository.postUserTransport(1L, mockkClass(UserTransportPostDto::class))
        runCurrent()

        //then
        coVerify(exactly = 1) { transportService.postUserTransport(any(), any()) }
    }

    @Test(expected = HttpException::class)
    fun `verify transport posted error`() = scope.runTest {
        //given
        val response = Response.error<Void>(404, "".toResponseBody())
        coEvery { transportService.postUserTransport(any(), any()) } returns response

        //when
        repository.postUserTransport(1L, mockkClass(UserTransportPostDto::class))
        runCurrent()
    }

    @Test
    fun `verify transport correctly updated`() = scope.runTest {
        //given
        val response = Response.success(mockkClass(Void::class))
        coEvery { transportService.updateUserTransport(any(), any()) } returns response

        //when
        repository.updateUserTransport(1L, mockkClass(UserTransportPostDto::class))
        runCurrent()

        //then
        coVerify(exactly = 1) { transportService.updateUserTransport(any(), any()) }
    }

    @Test(expected = HttpException::class)
    fun `verify group updated error`() = scope.runTest {
        //given
        val response = Response.error<Void>(404, "".toResponseBody())
        coEvery { transportService.updateUserTransport(any(), any()) } returns response

        //when
        repository.updateUserTransport(1L, mockkClass(UserTransportPostDto::class))
        runCurrent()
    }

    @Test
    fun `verify transport correctly deleted`() = scope.runTest {
        //given
        val response = Response.success(mockkClass(Void::class))
        coEvery { transportService.deleteUserTransport(any(), any()) } returns response

        //when
        repository.deleteUserTransport(1L, 1L)
        runCurrent()

        //then
        coVerify(exactly = 1) { transportService.deleteUserTransport(any(), any()) }
    }

    @Test(expected = HttpException::class)
    fun `verify group deleted error`() = scope.runTest {
        //given
        val response = Response.error<Void>(404, "".toResponseBody())
        coEvery { transportService.deleteUserTransport(any(), any()) } returns response

        //when
        repository.deleteUserTransport(1L, 1L)
        runCurrent()
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

}