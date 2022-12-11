package com.example.trip.usecase.availability

import com.example.trip.dto.SharedGroupAvailabilityDto
import com.example.trip.dto.UserGroupDto
import com.example.trip.models.Availability
import com.example.trip.models.OptimalAvailability
import com.example.trip.models.Resource
import com.example.trip.repositories.AvailabilityRepository
import com.example.trip.repositories.GroupsRepository
import com.example.trip.usecases.availability.GetOptimalDatesUseCase
import io.mockk.*
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
class GetOptimalAvailabilitiesUseCaseTest {

    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    private lateinit var useCase: GetOptimalDatesUseCase
    private val availabilityRepository: AvailabilityRepository = mockk()
    private val groupsRepository: GroupsRepository = mockk()
    private val group = mockkClass(UserGroupDto::class)
    private val availabilityZeroUsers = SharedGroupAvailabilityDto(
        1L,
        1L,
        listOf(),
        LocalDate.of(2022, 10, 10),
        LocalDate.of(2022, 10, 15),
        1
    )

    private val availabilityUsers = SharedGroupAvailabilityDto(
        2L,
        1L,
        listOf(1, 2),
        LocalDate.of(2022, 10, 10),
        LocalDate.of(2022, 10, 15),
        1
    )

    @Before
    fun setUp() {
        useCase = GetOptimalDatesUseCase(availabilityRepository, groupsRepository)
    }

    @Test
    fun `verify availability correctly mapped`() = scope.runTest {
        //given
        coEvery { availabilityRepository.getOptimalDates(any()) } returns listOf(
            availabilityUsers,
            availabilityZeroUsers
        )
        coEvery { groupsRepository.getGroup(any()) } returns group
        every { group.selectedSharedAvailability } returns 2L

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
        assertEquals(Resource.Loading<List<*>>(), result.first())
        assertEquals(Resource.Success(listOf(availability)), result.last())
    }


    @Test
    fun `verify result failure Http`() = scope.runTest {
        //given
        coEvery { availabilityRepository.getOptimalDates(any()) } throws HttpException(Response.error<List<*>>(404, "".toResponseBody()))
        coEvery { groupsRepository.getGroup(any()) } returns group
        every { group.selectedSharedAvailability } returns 2L

        //when
        val result = useCase(1L)
        runCurrent()

        //then
        assertEquals(Resource.Loading<List<*>>(), result.first())
        assertEquals(Resource.Failure<List<*>>(404), result.last())
    }

    @Test
    fun `verify result failure other`() = scope.runTest {
        //given
        coEvery { availabilityRepository.getOptimalDates(any()) } throws Exception()
        coEvery { groupsRepository.getGroup(any()) } returns group
        every { group.selectedSharedAvailability } returns 2L

        //when
        val result = useCase(1L)
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