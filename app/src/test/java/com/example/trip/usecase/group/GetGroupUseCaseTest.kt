package com.example.trip.usecase.group

import com.example.trip.dto.GroupStage
import com.example.trip.dto.UserDto
import com.example.trip.dto.UserGroupDto
import com.example.trip.models.Group
import com.example.trip.models.GroupStatus
import com.example.trip.models.Resource
import com.example.trip.repositories.GroupsRepository
import com.example.trip.usecases.group.GetGroupUseCase
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
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class GetGroupUseCaseTest {

    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    private lateinit var useCase: GetGroupUseCase
    private val groupsRepository: GroupsRepository = mockk()
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
        null,
        null,
        1
    )

    @Before
    fun setUp() {
        useCase = GetGroupUseCase(groupsRepository)
    }

    @Test
    fun `verify group correctly acquired`() = scope.runTest {
        //given
        coEvery { groupsRepository.getGroup(any()) } returns groupDto
        coEvery { groupsRepository.getCoordinators(any()) } returns listOf(UserDto(1, "", "1 1", "a", "a"))

        val group =
            Group(
                1L,
                "",
                GroupStatus.PLANNING,
                "",
                "PLN",
                1,
                1,
                null,
                1,
                null,
                null,
                listOf(1L)
            )

        //when
        val result = useCase(1L)
        runCurrent()

        //then
        assertEquals(Resource.Success(group), result)
    }

    @Test
    fun `verify result failure Http`() = scope.runTest {
        //given
        coEvery { groupsRepository.getGroup(any()) } throws HttpException(Response.error<List<*>>(403, "".toResponseBody()))
        coEvery { groupsRepository.getCoordinators(any()) } returns listOf()

        //when
        val result = useCase(1L)
        runCurrent()

        //then
        assertEquals(Resource.Failure<List<*>>(403), result)
    }

    @Test
    fun `verify result failure other`() = scope.runTest {
        //given
        coEvery { groupsRepository.deleteGroup(any()) } throws Exception()
        coEvery { groupsRepository.getCoordinators(any()) } returns listOf()

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