package com.example.trip.usecase.summary

import com.example.trip.models.Resource
import com.example.trip.repositories.AvailabilityRepository
import com.example.trip.usecases.summary.DeleteAcceptedAvailabilityUseCase
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
class DeleteAcceptedAvailabilityUseCaseTest {

    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    private lateinit var useCase: DeleteAcceptedAvailabilityUseCase
    private val availabilityRepository: AvailabilityRepository = mockk()

    @Before
    fun setUp() {
        useCase = DeleteAcceptedAvailabilityUseCase(availabilityRepository)
    }

    @Test
    fun `verify availability correctly deleted`() = scope.runTest {
        //given
        coEvery { availabilityRepository.deleteAcceptedAvailability(any()) } returns Unit

        //when
        val result = useCase(1L)
        runCurrent()

        //then
        assertEquals(Resource.Success(Unit), result)
    }

    @Test
    fun `verify result failure Http`() = scope.runTest {
        //given
        coEvery { availabilityRepository.deleteAcceptedAvailability(any()) } throws HttpException(Response.error<List<*>>(403, "".toResponseBody()))

        //when
        val result = useCase(1L)
        runCurrent()

        //then
        assertEquals(Resource.Failure<List<*>>(403), result)
    }

    @Test
    fun `verify result failure other`() = scope.runTest {
        //given
        coEvery { availabilityRepository.deleteAcceptedAvailability(any()) }throws Exception()

        //when
        val result = useCase(1L)
        runCurrent()

        //then
        assertEquals(Resource.Failure<List<*>>(0), result)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }


}