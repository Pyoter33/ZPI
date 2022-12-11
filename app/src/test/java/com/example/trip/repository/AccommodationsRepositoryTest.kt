package com.example.trip.repository

import com.example.trip.dto.*
import com.example.trip.repositories.AccommodationsRepository
import com.example.trip.service.AccommodationService
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

@OptIn(ExperimentalCoroutinesApi::class)
class AccommodationsRepositoryTest {

    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    private lateinit var repository: AccommodationsRepository
    private val accommodationService: AccommodationService = mockk()
    private val tripGroupService: TripGroupService = mockk()

    @Before
    fun setUp() {
        repository = AccommodationsRepository(accommodationService, tripGroupService)
    }

    @Test
    fun `verify accommodations correctly acquired`() = scope.runTest {
        //given
        val value = listOf(mockkClass(AccommodationDto::class))
        val response = Response.success(value)
        coEvery { accommodationService.getAccommodationsList(any()) } returns response

        //when
        val result = repository.getAccommodationsList(1L)
        runCurrent()

        //then
        assertEquals(value, result)
    }

    @Test(expected = HttpException::class)
    fun `verify accommodations error`() = scope.runTest {
        //given
        val response = Response.error<List<AccommodationDto>>(404, "".toResponseBody())
        coEvery { accommodationService.getAccommodationsList(any()) } returns response

        //when
        repository.getAccommodationsList(1L)
        runCurrent()
    }

    @Test
    fun `verify accommodation correctly acquired`() = scope.runTest {
        //given
        val value = mockkClass(AccommodationDto::class)
        val response = Response.success(value)
        coEvery { accommodationService.getAccommodation(any()) } returns response

        //when
        val result = repository.getAccommodation(1L)
        runCurrent()

        //then
        assertEquals(value, result)
    }

    @Test(expected = HttpException::class)
    fun `verify accommodation error`() = scope.runTest {
        //given
        val response = Response.error<AccommodationDto>(404, "".toResponseBody())
        coEvery { accommodationService.getAccommodation(any()) } returns response

        //when
        repository.getAccommodation(1L)
        runCurrent()
    }

    @Test
    fun `verify accommodation correctly posted`() = scope.runTest {
        //given
        val response = Response.success(mockkClass(Void::class))
        coEvery { accommodationService.postAccommodation(any()) } returns response

        //when
        repository.postAccommodation(mockkClass(AccommodationPostDto::class))
        runCurrent()

        //then
        coVerify(exactly = 1) { accommodationService.postAccommodation(any()) }
    }

    @Test(expected = HttpException::class)
    fun `verify accommodation posted error`() = scope.runTest {
        //given
        val response = Response.error<Void>(404, "".toResponseBody())
        coEvery { accommodationService.postAccommodation(any()) } returns response

        //when
        repository.postAccommodation(mockkClass(AccommodationPostDto::class))
        runCurrent()
    }

    @Test
    fun `verify accommodation correctly updated`() = scope.runTest {
        //given
        val response = Response.success(mockkClass(Void::class))
        coEvery { accommodationService.updateAccommodation(any(), any(), any()) } returns response

        //when
        repository.updateAccommodation(1L, 1L, mockkClass(AccommodationPostDto::class))
        runCurrent()

        //then
        coVerify(exactly = 1) { accommodationService.updateAccommodation(any(), any(), any()) }
    }

    @Test(expected = HttpException::class)
    fun `verify accommodation updated error`() = scope.runTest {
        //given
        val response = Response.error<Void>(404, "".toResponseBody())
        coEvery { accommodationService.updateAccommodation(any(), any(), any()) } returns response

        //when
        repository.updateAccommodation(1L, 1L, mockkClass(AccommodationPostDto::class))
        runCurrent()
    }

    @Test
    fun `verify accommodation correctly deleted`() = scope.runTest {
        //given
        val response = Response.success(mockkClass(Void::class))
        coEvery { accommodationService.deleteAccommodation(any()) } returns response

        //when
        repository.deleteAccommodation(1L)
        runCurrent()

        //then
        coVerify(exactly = 1) { accommodationService.deleteAccommodation(any()) }
    }

    @Test(expected = HttpException::class)
    fun `verify accommodation deleted error`() = scope.runTest {
        //given
        val response = Response.error<Void>(404, "".toResponseBody())
        coEvery { accommodationService.deleteAccommodation(any()) } returns response

        //when
        repository.deleteAccommodation(1L)
        runCurrent()
    }

    @Test
    fun `verify votes correctly acquired`() = scope.runTest {
        //given
        val value = listOf(mockkClass(AccommodationVoteDto::class))
        val response = Response.success(value)
        coEvery { accommodationService.getVotes(any()) } returns response

        //when
        val result = repository.getVotes(1L)
        runCurrent()

        //then
        assertEquals(value, result)
    }

    @Test(expected = HttpException::class)
    fun `verify votes error`() = scope.runTest {
        //given
        val response = Response.error<List<AccommodationVoteDto>>(404, "".toResponseBody())
        coEvery { accommodationService.getVotes(any()) } returns response

        //when
        repository.getVotes(1L)
        runCurrent()
    }

    @Test
    fun `verify vote correctly posted`() = scope.runTest {
        //given
        val response = Response.success(mockkClass(Void::class))
        coEvery { accommodationService.postVote(any()) } returns response

        //when
        repository.postVote(mockkClass(AccommodationVotePostDto::class))
        runCurrent()

        //then
        coVerify(exactly = 1) { accommodationService.postVote(any()) }
    }

    @Test(expected = HttpException::class)
    fun `verify vote posted error`() = scope.runTest {
        //given
        val response = Response.error<Void>(404, "".toResponseBody())
        coEvery { accommodationService.postVote(any()) } returns response

        //when
        repository.postVote(mockkClass(AccommodationVotePostDto::class))
        runCurrent()
    }

    @Test
    fun `verify vote correctly deleted`() = scope.runTest {
        //given
        val response = Response.success(mockkClass(Void::class))
        coEvery { accommodationService.deleteVote(any()) } returns response

        //when
        repository.deleteVote(mockkClass(AccommodationVoteId::class))
        runCurrent()

        //then
        coVerify(exactly = 1) { accommodationService.deleteVote(any()) }
    }

    @Test(expected = HttpException::class)
    fun `verify vote deleted error`() = scope.runTest {
        //given
        val response = Response.error<Void>(404, "".toResponseBody())
        coEvery { accommodationService.deleteVote(any()) } returns response

        //when
        repository.deleteVote(mockkClass(AccommodationVoteId::class))
        runCurrent()
    }

    @Test
    fun `verify accepted accommodation correctly updated`() = scope.runTest {
        //given
        val response = Response.success(mockkClass(Void::class))
        coEvery { accommodationService.acceptAccommodation(any()) } returns response

        //when
        repository.updateAcceptedAccommodation(1L)
        runCurrent()

        //then
        coVerify(exactly = 1) { accommodationService.acceptAccommodation(any()) }
    }

    @Test(expected = HttpException::class)
    fun `verify accepted accommodation accepted error`() = scope.runTest {
        //given
        val response = Response.error<Void>(404, "".toResponseBody())
        coEvery { accommodationService.acceptAccommodation(any()) } returns response

        //when
        repository.updateAcceptedAccommodation(1L)
        runCurrent()
    }

    @Test
    fun `verify accepted accommodation correctly deleted`() = scope.runTest {
        //given
        val response = Response.success(mockkClass(Void::class))
        coEvery { tripGroupService.deleteAcceptedAccommodation(any()) } returns response

        //when
        repository.deleteAcceptedAccommodation(1L)
        runCurrent()

        //then
        coVerify(exactly = 1) { tripGroupService.deleteAcceptedAccommodation(any()) }
    }

    @Test(expected = HttpException::class)
    fun `verify accepted accommodation deleted error`() = scope.runTest {
        //given
        val response = Response.error<Void>(404, "".toResponseBody())
        coEvery {  tripGroupService.deleteAcceptedAccommodation(any()) } returns response

        //when
        repository.deleteAcceptedAccommodation(1L)
        runCurrent()
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

}