package com.example.trip.repository

import com.example.trip.dto.TripGroupPostDto
import com.example.trip.dto.UserDto
import com.example.trip.dto.UserGroupDto
import com.example.trip.repositories.GroupsRepository
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
class GroupsRepositoryTest {

    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    private lateinit var repository: GroupsRepository
    private val groupService: TripGroupService = mockk()

    @Before
    fun setUp() {
        repository = GroupsRepository(groupService)
    }

    @Test
    fun `verify groups correctly acquired`() = scope.runTest {
        //given
        val value = listOf(mockkClass(UserGroupDto::class))
        val response = Response.success(value)
        coEvery { groupService.getGroups(any()) } returns response

        //when
        val result = repository.getGroups(1L)
        runCurrent()

        //then
        assertEquals(value, result)
    }

    @Test(expected = HttpException::class)
    fun `verify groups acquired error`() = scope.runTest {
        //given
        val response = Response.error<List<UserGroupDto>>(404, "".toResponseBody())
        coEvery { groupService.getGroups(any()) } returns response

        //when
       repository.getGroups(1L)
        runCurrent()
    }

    @Test
    fun `verify one group correctly acquired`() = scope.runTest {
        //given
        val value = mockkClass(UserGroupDto::class)
        val response = Response.success(value)
        coEvery { groupService.getGroupById(any()) } returns response

        //when
        val result = repository.getGroup(1L)
        runCurrent()

        //then
        assertEquals(value, result)
    }

    @Test(expected = HttpException::class)
    fun `verify one group acquired error`() = scope.runTest {
        //given
        val response = Response.error<UserGroupDto>(404, "".toResponseBody())
        coEvery { groupService.getGroupById(any()) } returns response

        //when
        repository.getGroup(1L)
        runCurrent()
    }

    @Test
    fun `verify coordinators correctly acquired`() = scope.runTest {
        //given
        val value = listOf(mockkClass(UserDto::class))
        val response = Response.success(value)
        coEvery { groupService.getCoordinators(any()) } returns response

        //when
        val result = repository.getCoordinators(1L)
        runCurrent()

        //then
        assertEquals(value, result)
    }

    @Test(expected = HttpException::class)
    fun `verify coordinators acquired error`() = scope.runTest {
        //given
        val response = Response.error<List<UserDto>>(404, "".toResponseBody())
        coEvery { groupService.getCoordinators(any()) } returns response

        //when
        repository.getCoordinators(1L)
        runCurrent()
    }

    @Test
    fun `verify group correctly posted`() = scope.runTest {
        //given
        val response = Response.success(mockkClass(Void::class))
        coEvery { groupService.postGroup(any()) } returns response

        //when
        repository.postGroup(mockkClass(TripGroupPostDto::class))
        runCurrent()

        //then
        coVerify(exactly = 1) { groupService.postGroup(any()) }
    }

    @Test(expected = HttpException::class)
    fun `verify group posted error`() = scope.runTest {
        //given
        val response = Response.error<Void>(404, "".toResponseBody())
        coEvery { groupService.postGroup(any()) } returns response

        //when
        repository.postGroup(mockkClass(TripGroupPostDto::class))
        runCurrent()
    }

    @Test
    fun `verify group correctly updated`() = scope.runTest {
        //given
        val response = Response.success(mockkClass(Void::class))
        coEvery { groupService.updateGroup(any(), any()) } returns response

        //when
        repository.updateGroup(1L, mockkClass(TripGroupPostDto::class))
        runCurrent()

        //then
        coVerify(exactly = 1) { groupService.updateGroup(any(), any()) }
    }

    @Test(expected = HttpException::class)
    fun `verify group updated error`() = scope.runTest {
        //given
        val response = Response.error<Void>(404, "".toResponseBody())
        coEvery { groupService.updateGroup(any(), any()) } returns response

        //when
        repository.updateGroup(1L, mockkClass(TripGroupPostDto::class))
        runCurrent()
    }

    @Test
    fun `verify group correctly deleted`() = scope.runTest {
        //given
        val response = Response.success(mockkClass(Void::class))
        coEvery { groupService.deleteGroup(any()) } returns response

        //when
        repository.deleteGroup(1L)
        runCurrent()

        //then
        coVerify(exactly = 1) { groupService.deleteGroup(any()) }
    }

    @Test(expected = HttpException::class)
    fun `verify group deleted error`() = scope.runTest {
        //given
        val response = Response.error<Void>(404, "".toResponseBody())
        coEvery { groupService.deleteGroup(any()) } returns response

        //when
        repository.deleteGroup(1L)
        runCurrent()
    }

    @Test
    fun `verify group correctly left`() = scope.runTest {
        //given
        val response = Response.success(mockkClass(Void::class))
        coEvery { groupService.leaveGroup(any()) } returns response

        //when
        repository.leaveGroup(1L)
        runCurrent()

        //then
        coVerify(exactly = 1) { groupService.leaveGroup(any()) }
    }

    @Test(expected = HttpException::class)
    fun `verify group left error`() = scope.runTest {
        //given
        val response = Response.error<Void>(404, "".toResponseBody())
        coEvery { groupService.leaveGroup(any()) } returns response

        //when
        repository.leaveGroup(1L)
        runCurrent()
    }

    @Test
    fun `verify group stage correctly changed`() = scope.runTest {
        //given
        val response = Response.success(mockkClass(Void::class))
        coEvery { groupService.changeGroupStage(any()) } returns response

        //when
        repository.changeGroupState(1L)
        runCurrent()

        //then
        coVerify(exactly = 1) { groupService.changeGroupStage(any()) }
    }

    @Test(expected = HttpException::class)
    fun `verify group stage changed error`() = scope.runTest {
        //given
        val response = Response.error<Void>(404, "".toResponseBody())
        coEvery { groupService.changeGroupStage(any()) } returns response

        //when
        repository.changeGroupState(1L)
        runCurrent()
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

}