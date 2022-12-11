package com.example.trip.repository

import com.example.trip.dto.AvailabilityDto
import com.example.trip.dto.AvailabilityPostDto
import com.example.trip.dto.SharedGroupAvailabilityDto
import com.example.trip.models.Availability
import com.example.trip.repositories.AvailabilityRepository
import com.example.trip.service.AvailabilityService
import com.example.trip.service.TripGroupService
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
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class AvailabilityRepositoryTest {

    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    private lateinit var repository: AvailabilityRepository
    private val availabilityService: AvailabilityService = mockk()
    private val tripGroupService: TripGroupService = mockk()

    @Before
    fun setUp() {
        repository = AvailabilityRepository(availabilityService, tripGroupService)
    }

    @Test
    fun `verify availabilities correctly acquired`() = scope.runTest {
        //given
        val value = listOf(mockkClass(AvailabilityDto::class))
        val response = Response.success(value)
        coEvery { availabilityService.getAvailabilitiesForUser(any(), any()) } returns response

        //when
        val result = repository.getUserAvailabilities(1L, 1L)
        runCurrent()

        //then
        assertEquals(value, result)
    }

    @Test(expected = HttpException::class)
    fun `verify availabilities error`() = scope.runTest {
        //given
        val response = Response.error<List<AvailabilityDto>>(404, "".toResponseBody())
        coEvery { availabilityService.getAvailabilitiesForUser(any(), any()) } returns response

        //when
        repository.getUserAvailabilities(1L, 1L)
        runCurrent()
    }

    @Test
    fun `verify accepted availability correctly acquired`() = scope.runTest {
        //given
        val value = mockkClass(SharedGroupAvailabilityDto::class)
        val response = Response.success(value)
        coEvery { availabilityService.getAcceptedAvailability(any()) } returns response

        //when
        val result = repository.getAcceptedAvailability(1L)
        runCurrent()

        //then
        assertEquals(value, result)
    }

    @Test(expected = HttpException::class)
    fun `verify accepted availability error`() = scope.runTest {
        //given
        val response = Response.error<SharedGroupAvailabilityDto>(404, "".toResponseBody())
        coEvery { availabilityService.getAcceptedAvailability(any()) } returns response

        //when
        repository.getAcceptedAvailability(1L)
        runCurrent()
    }

    @Test
    fun `verify optimal availabilities correctly acquired`() = scope.runTest {
        //given
        val value = listOf(mockkClass(SharedGroupAvailabilityDto::class))
        val response = Response.success(value)
        coEvery { availabilityService.getAvailabilitiesForGroup(any()) } returns response

        //when
        val result = repository.getOptimalDates(1L)
        runCurrent()

        //then
        assertEquals(value, result)
    }

    @Test(expected = HttpException::class)
    fun `verify optimal availabilities error`() = scope.runTest {
        //given
        val response = Response.error<List<SharedGroupAvailabilityDto>>(404, "".toResponseBody())
        coEvery { availabilityService.getAvailabilitiesForGroup(any()) } returns response

        //when
        repository.getOptimalDates(1L)
        runCurrent()
    }

    @Test
    fun `verify accepted availability correctly posted`() = scope.runTest {
        //given
        val response = Response.success(mockkClass(Void::class))
        coEvery {
            availabilityService.postAcceptedAvailability(
                any(),
                any(),
                any()
            )
        } returns response

        //when
        repository.postAcceptedAvailability(
            1L,
            Availability(1L, 1L, LocalDate.now(), LocalDate.now())
        )
        runCurrent()

        //then
        coVerify(exactly = 1) { availabilityService.postAcceptedAvailability(any(), any(), any()) }
    }

    @Test(expected = HttpException::class)
    fun `verify accepted availability posted error`() = scope.runTest {
        //given
        val response = Response.error<Void>(404, "".toResponseBody())
        coEvery {
            availabilityService.postAcceptedAvailability(
                any(),
                any(),
                any()
            )
        } returns response

        //when
        repository.postAcceptedAvailability(
            1L,
            Availability(1L, 1L, LocalDate.now(), LocalDate.now())
        )
        runCurrent()
    }

    @Test
    fun `verify availability correctly accepted`() = scope.runTest {
        //given
        val response = Response.success(mockkClass(Void::class))
        coEvery { availabilityService.acceptAvailability(any()) } returns response

        //when
        repository.acceptAvailability(1L)
        runCurrent()

        //then
        coVerify(exactly = 1) { availabilityService.acceptAvailability(any()) }
    }

    @Test(expected = HttpException::class)
    fun `verify availability accepted error`() = scope.runTest {
        //given
        val response = Response.error<Void>(404, "".toResponseBody())
        coEvery {
            availabilityService.postAcceptedAvailability(
                any(),
                any(),
                any()
            )
        } returns response

        //when
        repository.postAcceptedAvailability(
            1L,
            Availability(1L, 1L, LocalDate.now(), LocalDate.now())
        )
        runCurrent()
    }

    @Test
    fun `verify accepted availability correctly deleted`() = scope.runTest {
        //given
        val response = Response.success(mockkClass(Void::class))
        coEvery { tripGroupService.deleteAcceptedAvailability(any()) } returns response

        //when
        repository.deleteAcceptedAvailability(1L)
        runCurrent()

        //then
        coVerify(exactly = 1) { tripGroupService.deleteAcceptedAvailability(any()) }
    }

    @Test(expected = HttpException::class)
    fun `verify accepted availability deleted error`() = scope.runTest {
        //given
        val response = Response.error<Void>(404, "".toResponseBody())
        coEvery { tripGroupService.deleteAcceptedAvailability(any()) } returns response

        //when
        repository.deleteAcceptedAvailability(1L)
        runCurrent()
    }

    @Test
    fun `verify availability correctly deleted`() = scope.runTest {
        //given
        val response = Response.success(mockkClass(Void::class))
        coEvery { availabilityService.deleteAvailability(any(), any()) } returns response

        //when
        repository.deleteAvailability(1L, 1L)
        runCurrent()

        //then
        coVerify(exactly = 1) { availabilityService.deleteAvailability(any(), any()) }
    }

    @Test(expected = HttpException::class)
    fun `verify availability deleted error`() = scope.runTest {
        //given
        val response = Response.error<Void>(404, "".toResponseBody())
        coEvery { availabilityService.deleteAvailability(any(), any()) } returns response

        //when
        repository.deleteAvailability(1L, 1L)
        runCurrent()
    }

    @Test
    fun `verify availability correctly posted`() = scope.runTest {
        //given
        val response = Response.success(mockkClass(Void::class))
        coEvery { availabilityService.postAvailability(any()) } returns response

        //when
        repository.postAvailability(AvailabilityPostDto(1L, 1L, LocalDate.now(), LocalDate.now()))
        runCurrent()

        //then
        coVerify(exactly = 1) { availabilityService.postAvailability(any()) }
    }

    @Test(expected = HttpException::class)
    fun `verify availability posted error`() = scope.runTest {
        //given
        val response = Response.error<Void>(404, "".toResponseBody())
        coEvery { availabilityService.postAvailability(any()) } returns response

        //when
        repository.postAvailability(AvailabilityPostDto(1L, 1L, LocalDate.now(), LocalDate.now()))
        runCurrent()
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

}