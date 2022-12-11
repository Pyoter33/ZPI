package com.example.trip.repository

import com.example.trip.Constants
import com.example.trip.dto.AppUserDto
import com.example.trip.dto.UserDto
import com.example.trip.repositories.ParticipantsRepository
import com.example.trip.service.TripGroupService
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class ParticipantsRepositoryTest {

    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    private lateinit var repository: ParticipantsRepository
    private val groupService: TripGroupService = mockk()

    @Before
    fun setUp() {
        repository = ParticipantsRepository(groupService)
    }

    @Test
    fun `verify invite correctly acquired with header`() = scope.runTest {
        //given
        val value = mockkClass(UserDto::class)
        val response = mockkClass(Response::class)
        every { response.isSuccessful } returns true
        every { response.body() } returns value
        every { response.headers()[Constants.LOCATION_KEY] } returns ""
        coEvery { groupService.getInvitation(any()) } returns response as Response<Void>

        //when
        val result = repository.getInviteLink(1L)
        runCurrent()

        //then
        Assert.assertEquals("", result)
    }

    @Test(expected = HttpException::class)
    fun `verify invite correctly acquired with no header`() = scope.runTest {
        //given
        val value = mockkClass(UserDto::class)
        val response = mockkClass(Response::class)
        every { response.isSuccessful } returns true
        every { response.code() } returns 404
        every { response.message() } returns ""
        every { response.body() } returns value
        every { response.headers()[Constants.LOCATION_KEY] } returns null
        coEvery { groupService.getInvitation(any()) } returns response as Response<Void>

        //when
        repository.getInviteLink(1L)
        runCurrent()
    }

    @Test
    fun `verify participants correctly acquired`() = scope.runTest {
        //given
        val value = listOf(mockkClass(UserDto::class))
        val response = Response.success(value)
        coEvery { groupService.getParticipants(any()) } returns response

        //when
        val result = repository.getParticipantsForGroup(1L)
        runCurrent()

        //then
        assertEquals(value, result)
    }

    @Test(expected = HttpException::class)
    fun `verify participants acquired error`() = scope.runTest {
        //given
        val response = Response.error<List<UserDto>>(404, "".toResponseBody())
        coEvery { groupService.getParticipants(any()) } returns response

        //when
        repository.getParticipantsForGroup(1L)
        runCurrent()
    }

    @Test
    fun `verify user correctly acquired`() = scope.runTest {
        //given
        val value = mockkClass(AppUserDto::class)
        val response = Response.success(value)
        coEvery { groupService.getUser(any()) } returns response

        //when
        val result = repository.getUser(1L)
        runCurrent()

        //then
        assertEquals(value, result)
    }

    @Test(expected = HttpException::class)
    fun `verify user acquired error`() = scope.runTest {
        //given
        val response = Response.error<AppUserDto>(404, "".toResponseBody())
        coEvery { groupService.getUser(any()) } returns response

        //when
        repository.getUser(1L)
        runCurrent()
    }

    @Test
    fun `verify user correctly updated`() = scope.runTest {
        //given
        val response = Response.success(mockkClass(Void::class))
        coEvery { groupService.updateUser(any()) } returns response

        //when
        repository.updateParticipant(mockkClass(AppUserDto::class))
        runCurrent()

        //then
        coVerify(exactly = 1) { groupService.updateUser(any()) }
    }

    @Test(expected = HttpException::class)
    fun `verify user updated error`() = scope.runTest {
        //given
        val response = Response.error<Void>(404, "".toResponseBody())
        coEvery { groupService.updateUser(any()) } returns response

        //when
        repository.updateParticipant(mockkClass(AppUserDto::class))
        runCurrent()
    }

    @Test
    fun `verify coordinator correctly posted`() = scope.runTest {
        //given
        val response = Response.success(mockkClass(Void::class))
        coEvery { groupService.putCoordinator(any(), any()) } returns response

        //when
        repository.postCoordinator(1L, 1L)
        runCurrent()

        //then
        coVerify(exactly = 1) { groupService.putCoordinator(any(), any()) }
    }

    @Test(expected = HttpException::class)
    fun `verify coordinator posted error`() = scope.runTest {
        //given
        val response = Response.error<Void>(404, "".toResponseBody())
        coEvery { groupService.putCoordinator(any(), any()) } returns response

        //when
        repository.postCoordinator(1L, 1L)
        runCurrent()
    }

    @Test
    fun `verify participant correctly deleted`() = scope.runTest {
        //given
        val response = Response.success(mockkClass(Void::class))
        coEvery { groupService.deleteParticipant(any(), any()) } returns response

        //when
        repository.deleteParticipant(1L, 1L)
        runCurrent()

        //then
        coVerify(exactly = 1) { groupService.deleteParticipant(any(), any()) }
    }

    @Test(expected = HttpException::class)
    fun `verify participant deleted error`() = scope.runTest {
        //given
        val response = Response.error<Void>(404, "".toResponseBody())
        coEvery { groupService.deleteParticipant(any(), any()) } returns response

        //when
        repository.deleteParticipant(1L, 1L)
        runCurrent()
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

}