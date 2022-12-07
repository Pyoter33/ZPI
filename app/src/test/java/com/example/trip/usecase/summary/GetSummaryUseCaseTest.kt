package com.example.trip.usecase.summary

import com.example.trip.dto.*
import com.example.trip.models.*
import com.example.trip.repositories.AccommodationsRepository
import com.example.trip.repositories.AvailabilityRepository
import com.example.trip.repositories.GroupsRepository
import com.example.trip.repositories.ParticipantsRepository
import com.example.trip.usecases.summary.GetSummaryUseCase
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
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class GetSummaryUseCaseTest {

    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    private lateinit var useCase: GetSummaryUseCase
    private val accommodationsRepository: AccommodationsRepository = mockk()
    private val participantsRepository: ParticipantsRepository = mockk()
    private val availabilityRepository: AvailabilityRepository = mockk()
    private val groupsRepository: GroupsRepository = mockk()
    private val group = mockkClass(UserGroupDto::class)
    private val accommodationDto = AccommodationDto(
        1L,
        1L,
        1L,
        "",
        "a",
        "b",
        "d",
        "c",
        null,
        "",
        "",
        1,
        BigDecimal.ZERO,
        0.0,
        0.0
    )

    private val availabilityDto = SharedGroupAvailabilityDto(
        1L,
        1L,
        listOf(1, 2),
        LocalDate.of(2022, 10, 10),
        LocalDate.of(2022, 10, 15),
        1
    )

    private val groupDto = UserGroupDto(
        1L,
        "",
        Currency.getInstance("PLN"),
        null,
        "",
        "",
        null,
        null,
        0.0,
        0.0,
        GroupStage.PLANNING_STAGE,
        1,
        1,
        1L,
        1L,
        1
    )


    @Before
    fun setUp() {
        useCase = GetSummaryUseCase(
            availabilityRepository,
            accommodationsRepository,
            participantsRepository,
            groupsRepository
        )
        every { group.selectedAccommodationId } returns 1L
        every { group.selectedSharedAvailability } returns 1L
    }

    @Test
    fun `verify accommodation correctly mapped voted`() = scope.runTest {
        //given
        coEvery { groupsRepository.getGroup(any()) } returns groupDto
        coEvery { accommodationsRepository.getAccommodation(any()) } returns accommodationDto
        coEvery { availabilityRepository.getAcceptedAvailability(any()) } returns availabilityDto
        coEvery { participantsRepository.getParticipantsForGroup(any()) } returns listOf(
            UserDto(1, "", "1 1", "a", "a")
        )
        coEvery { groupsRepository.getCoordinators(any()) } returns listOf(
            UserDto(
                1,
                "",
                "1 1",
                "a",
                "a"
            )
        )

        val accommodation =
            Accommodation(
                1L,
                1L,
                1L,
                "",
                "a",
                null,
                "",
                "",
                1,
                BigDecimal.ZERO,
                false,
                true
            )

        val availability =
            OptimalAvailability(
                Availability(
                    1L,
                    -1L,
                    LocalDate.of(2022, 10, 10),
                    LocalDate.of(2022, 10, 15)
                ),
                2,
                1,
                true
            )

        val participant = Participant(1, "a a", "", "1 1", UserRole.COORDINATOR)

        val summary = Summary(accommodation, availability, listOf(participant))

        //when
        val result = useCase(1L)
        runCurrent()

        //then
        assertEquals(Resource.Loading<List<*>>(), result.first())
        assertEquals(Resource.Success(summary), result.last())
    }

    @Test
    fun `verify result failure Http`() = scope.runTest {
        //given
        coEvery { accommodationsRepository.getAccommodation(any()) } throws HttpException(
            Response.error<List<*>>(404, "".toResponseBody())
        )
        coEvery { availabilityRepository.getAcceptedAvailability(any()) } returns availabilityDto
        coEvery { participantsRepository.getParticipantsForGroup(any()) } returns listOf(
            UserDto(1, "", "1 1", "a", "a")
        )
        coEvery { groupsRepository.getCoordinators(any()) } returns listOf(
            UserDto(
                1,
                "",
                "1 1",
                "a",
                "a"
            )
        )
        coEvery { groupsRepository.getGroup(any()) } returns groupDto

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
        coEvery { accommodationsRepository.getAccommodation(any()) } throws Exception()
        coEvery { availabilityRepository.getAcceptedAvailability(any()) } returns availabilityDto
        coEvery { participantsRepository.getParticipantsForGroup(any()) } returns listOf(
            UserDto(1, "", "1 1", "a", "a")
        )
        coEvery { groupsRepository.getCoordinators(any()) } returns listOf(
            UserDto(
                1,
                "",
                "1 1",
                "a",
                "a"
            )
        )
        coEvery { groupsRepository.getGroup(any()) } returns groupDto

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