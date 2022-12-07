package com.example.trip.usecase.availability

import com.example.trip.models.Resource
import com.example.trip.repositories.AvailabilityRepository
import com.example.trip.usecases.availability.DeleteAvailabilityUseCase
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
class DeleteAvailabilityUseCaseTest {

    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    private lateinit var useCase: DeleteAvailabilityUseCase
    private val availabilityRepository: AvailabilityRepository = mockk()

    @Before
    fun setUp() {
        useCase = DeleteAvailabilityUseCase(availabilityRepository)
    }

    @Test
    fun `verify vote correctly posted`() = scope.runTest {
        //given
        coEvery { availabilityRepository.deleteAvailability(any(), any()) } returns Unit

        //when
        val result = useCase(1L, 1L)
        runCurrent()

        //then
        assertEquals(Resource.Success(Unit), result)
    }

    @Test
    fun `verify result failure Http`() = scope.runTest {
        //given
        coEvery { availabilityRepository.deleteAvailability(any(), any()) } throws HttpException(Response.error<List<*>>(404, "".toResponseBody()))

        //when
        val result = useCase(1L, 1L)
        runCurrent()

        //then
        assertEquals(Resource.Failure<List<*>>(404), result)
    }

    @Test
    fun `verify result failure other`() = scope.runTest {
        //given
        coEvery { availabilityRepository.deleteAvailability(any(), any()) } throws Exception()

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