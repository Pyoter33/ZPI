package com.example.trip.usecase.transport

import com.example.trip.dto.*
import com.example.trip.models.*
import com.example.trip.repositories.AccommodationsRepository
import com.example.trip.repositories.GroupsRepository
import com.example.trip.repositories.TransportRepository
import com.example.trip.usecases.transport.GetTransportUseCase
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
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
class GetTransportUseCaseTest {

    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    private lateinit var useCase: GetTransportUseCase
    private val transportRepository: TransportRepository = mockk()
    private val groupsRepository: GroupsRepository = mockk()
    private val accommodationsRepository: AccommodationsRepository = mockk()
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

    private val carTransportDto = CarTransportDto(
        1L,
        Duration.ofMinutes(1),
        null,
        "",
        "",
        LocalDate.of(2020, 10, 10),
        LocalDate.of(2020, 10, 11),
        null,
        1
    )

    private val airTransportDto = AirTransportDto(
        2L,
        Duration.ofMinutes(1),
        null,
        "",
        "",
        LocalDate.of(2020, 10, 10),
        LocalDate.of(2020, 10, 11),
        null,
        listOf(
            FlightDto(
                1L,
                "",
                "",
                "",
                LocalDateTime.of(2020, 10, 10, 0, 0),
                LocalDateTime.of(2020, 10, 10, 0, 1),
                Duration.ofMinutes(1),
                Duration.ofMinutes(1),
                Duration.ofMinutes(1)
            )
        )
    )

    private val userTransportDto = UserTransportDto(
        3L,
        Duration.ofMinutes(1),
        BigDecimal.ONE,
        "",
        "",
        LocalDate.of(2020, 10, 10),
        LocalDate.of(2020, 10, 11),
        null,
        "",
        null,
        LocalDateTime.of(2020, 10, 10, 0, 0),
    )

    @Before
    fun setUp() {
        useCase = GetTransportUseCase(transportRepository, groupsRepository, accommodationsRepository)
        every { group.latitude } returns 0.0
        every { group.longitude } returns 0.0
    }

    @Test
    fun `verify transport correctly mapped`() = scope.runTest {
        //given
        coEvery { transportRepository.getTransport(any()) } returns listOf(airTransportDto, carTransportDto, userTransportDto)
        coEvery { accommodationsRepository.getAccommodation(any()) } returns accommodationDto
        coEvery { groupsRepository.getGroup(any()) } returns group

        val carTransport = CarTransport(
            1L,
            Duration.ofMinutes(1),
            "0.0,0.0",
            "0.0,0.0",
            1
        )

        val airTransport = AirTransport(
            2L,
            Duration.ofMinutes(1),
            "",
            "",
            null,
            listOf(
                Flight(
                    1L,
                    "",
                    "",
                    "",
                    LocalTime.of( 0, 0),
                    LocalTime.of( 0, 1),
                    Duration.ofMinutes(1),
                    Duration.ofMinutes(1),
                    Duration.ofMinutes(1)
                )
            )
        )

        val userTransport = UserTransport(
            3L,
            1L,
            1L,
            listOf(),
            Duration.ofMinutes(1),
            LocalDate.of(2020, 10, 10),
            LocalTime.of(0, 0),
            BigDecimal.ONE,
            "",
            "",
            null
        )

        val transport = Transport(carTransport, airTransport, mutableListOf(userTransport))

        //when
        val result = useCase(1L ,1L)
        runCurrent()

        //then
        assertEquals(Resource.Loading<List<*>>(), result.first())
        assertEquals(transport.carTransport, (result.last() as Resource.Success).data.carTransport)
        assertEquals(transport.airTransport, (result.last() as Resource.Success).data.airTransport)
    }

    @Test
    fun `verify result failure Http`() = scope.runTest {
        //given
        val error = """{"message":"error", "status": BAD_REQUEST, "timestamp": "..."}"""
        coEvery { transportRepository.getTransport(any()) } throws HttpException(
            Response.error<List<*>>(
                404,
                error.toResponseBody()
            )
        )
        coEvery { accommodationsRepository.getAccommodation(any()) } returns accommodationDto
        coEvery { groupsRepository.getGroup(any()) } returns group

        //when
        val result = useCase(1L ,1L)
        runCurrent()

        //then
        assertEquals(Resource.Loading<List<*>>(), result.first())
        assertEquals(Resource.Failure<List<*>>(404), result.last())
    }

    @Test
    fun `verify result failure other`() = scope.runTest {
        //given
        coEvery { transportRepository.getTransport(any()) } throws Exception()
        coEvery { accommodationsRepository.getAccommodation(any()) } returns accommodationDto
        coEvery { groupsRepository.getGroup(any()) } returns group

        //when
        val result = useCase(1L ,1L)
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