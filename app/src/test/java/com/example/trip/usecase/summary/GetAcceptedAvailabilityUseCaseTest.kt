package com.example.trip.usecase.summary

import com.example.trip.dto.SharedGroupAvailabilityDto
import com.example.trip.models.Availability
import com.example.trip.models.OptimalAvailability
import com.example.trip.models.Resource
import com.example.trip.repositories.AvailabilityRepository
import com.example.trip.repositories.GroupsRepository
import com.example.trip.usecases.summary.GetAcceptedAvailabilityUseCase
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
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class GetAcceptedAvailabilityUseCaseTest {

    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    private lateinit var useCase: GetAcceptedAvailabilityUseCase
    private val availabilityRepository: AvailabilityRepository = mockk()
    private val groupsRepository: GroupsRepository = mockk()
    private val availability = SharedGroupAvailabilityDto(
        2L,
        1L,
        listOf(1, 2),
        LocalDate.of(2022, 10, 10),
        LocalDate.of(2022, 10, 15),
        1
    )

    @Before
    fun setUp() {
        useCase = GetAcceptedAvailabilityUseCase(availabilityRepository, groupsRepository)
    }

    @Test
    fun `verify accepted accommodation correctly fetched`() = scope.runTest {
        //given
        coEvery { groupsRepository.getGroup(any()).selectedSharedAvailability } returns 1L
        coEvery { availabilityRepository.getAcceptedAvailability(any()) } returns availability

        val availability =
            OptimalAvailability(
                Availability(
                    2L,
                    -1L,
                    LocalDate.of(2022, 10, 10),
                    LocalDate.of(2022, 10, 15)
                ),
                2,
                1,
                true
            )

        //when
        val result = useCase(1L)
        runCurrent()

        //then
        assertEquals(Resource.Success(availability), result)
    }

    @Test
    fun `verify result failure Http`() = scope.runTest {
        //given
        coEvery { groupsRepository.getGroup(any()).selectedSharedAvailability } throws HttpException(Response.error<List<*>>(403, "".toResponseBody()))
        coEvery { availabilityRepository.getAcceptedAvailability(any()) } returns availability

        //when
        val result = useCase(1L)
        runCurrent()

        //then
        assertEquals(Resource.Failure<List<*>>(403), result)
    }

    @Test
    fun `verify result failure other`() = scope.runTest {
        //given
        coEvery { groupsRepository.getGroup(any()).selectedSharedAvailability } throws Exception()
        coEvery { availabilityRepository.getAcceptedAvailability(any()) } returns availability

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