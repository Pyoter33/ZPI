package com.example.trip.usecase.availability

import com.example.trip.dto.AvailabilityDto
import com.example.trip.models.Availability
import com.example.trip.models.Resource
import com.example.trip.repositories.AvailabilityRepository
import com.example.trip.usecases.availability.GetUserAvailabilitiesUseCase
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
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
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class GetUserAvailabilitiesUseCaseTest {

    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    private lateinit var useCase: GetUserAvailabilitiesUseCase
    private val availabilityRepository: AvailabilityRepository = mockk()
    private val availability = AvailabilityDto(
        1L,
        1L,
        1L,
        LocalDate.of(2022, 10, 10),
        LocalDate.of(2022, 10, 15)
    )

    @Before
    fun setUp() {
        useCase = GetUserAvailabilitiesUseCase(availabilityRepository)
    }

    @Test
    fun `verify availability correctly mapped`() = scope.runTest {
        //given
        coEvery { availabilityRepository.getUserAvailabilities(any(), any()) } returns listOf(availability)
        val availability =
            Availability(
                1L,
                1L,
                LocalDate.of(2022, 10, 10),
                LocalDate.of(2022, 10, 15)
            )

        //when
        val result = useCase(1L, 1L)
        runCurrent()

        //then
        assertEquals(Resource.Loading<List<*>>(), result.first())
        assertEquals(Resource.Success(listOf(availability)), result.last())
    }


    @Test
    fun `verify result failure Http`() = scope.runTest {
        //given
        coEvery { availabilityRepository.getUserAvailabilities(any(), any()) } throws HttpException(Response.error<List<*>>(404, "".toResponseBody()))

        //when
        val result = useCase(1L, 1L)
        runCurrent()

        //then
        assertEquals(Resource.Loading<List<*>>(), result.first())
        assertEquals(Resource.Failure<List<*>>(404), result.last())
    }

    @Test
    fun `verify result failure other`() = scope.runTest {
        //given
        coEvery { availabilityRepository.getUserAvailabilities(any(), any()) } throws Exception()

        //when
        val result = useCase(1L, 1L)
        runCurrent()

        //then
        assertEquals(Resource.Loading<List<*>>(), result.first())
        assertEquals(Resource.Failure<List<*>>(0), result.last())
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }
}