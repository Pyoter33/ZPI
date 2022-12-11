package com.example.trip.usecase.transport

import com.example.trip.dto.UserTransportPostDto
import com.example.trip.models.Resource
import com.example.trip.repositories.TransportRepository
import com.example.trip.usecases.transport.UpdateTransportUseCase
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
class UpdateTransportUseCaseTest {

    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    private lateinit var useCase: UpdateTransportUseCase
    private val transportRepository: TransportRepository = mockk()
    private val userTransportDto = mockkClass(UserTransportPostDto ::class)

    @Before
    fun setUp() {
        useCase = UpdateTransportUseCase(transportRepository)
    }

    @Test
    fun `verify transport correctly updated`() = scope.runTest {
        //given
        coEvery { transportRepository.updateUserTransport(any(), any()) } returns Unit

        //when
        val result = useCase(1L, userTransportDto)
        runCurrent()

        //then
        assertEquals(Resource.Success(Unit), result)
    }

    @Test
    fun `verify result failure Http`() = scope.runTest {
        //given
        coEvery { transportRepository.updateUserTransport(any(), any()) } throws HttpException(Response.error<List<*>>(403, "".toResponseBody()))

        //when
        val result = useCase(1L, userTransportDto)
        runCurrent()

        //then
        assertEquals(Resource.Failure<List<*>>(403), result)
    }

    @Test
    fun `verify result failure other`() = scope.runTest {
        //given
        coEvery { transportRepository.updateUserTransport(any(), any()) } throws Exception()

        //when
        val result = useCase(1L, userTransportDto)
        runCurrent()

        //then
        assertEquals(Resource.Failure<List<*>>(0), result)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }
}