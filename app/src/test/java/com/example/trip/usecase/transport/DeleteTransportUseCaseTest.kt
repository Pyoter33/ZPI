package com.example.trip.usecase.transport

import com.example.trip.models.Resource
import com.example.trip.repositories.TransportRepository
import com.example.trip.usecases.transport.DeleteTransportUseCase
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
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
class DeleteTransportUseCaseTest {

    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    private lateinit var useCase: DeleteTransportUseCase
    private val transportRepository: TransportRepository = mockk()

    @Before
    fun setUp() {
        useCase = DeleteTransportUseCase(transportRepository)
    }

    @Test
    fun `verify transport correctly deleted`() = scope.runTest {
        //given
        coEvery { transportRepository.deleteUserTransport(any(), any()) } returns Unit

        //when
        val result = useCase(1L, 1L)
        runCurrent()

        //then
        assertEquals(Resource.Success(Unit), result)
    }

    @Test
    fun `verify result failure Http`() = scope.runTest {
        //given
        coEvery { transportRepository.deleteUserTransport(any(), any()) } throws HttpException(Response.error<List<*>>(403, "".toResponseBody()))

        //when
        val result = useCase(1L, 1L)
        runCurrent()

        //then
        assertEquals(Resource.Failure<List<*>>(403), result)
    }

    @Test
    fun `verify result failure other`() = scope.runTest {
        //given
        coEvery { transportRepository.deleteUserTransport(any(), any()) } throws Exception()

        //when
        val result = useCase(1L, 1L)
        runCurrent()

        //then
        assertEquals(Resource.Failure<List<*>>(0), result)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }
}