package com.example.trip.usecase.group

import com.example.trip.dto.GroupStage
import com.example.trip.dto.UserDto
import com.example.trip.dto.UserGroupDto
import com.example.trip.models.Accommodation
import com.example.trip.models.Group
import com.example.trip.models.GroupStatus
import com.example.trip.models.Resource
import com.example.trip.repositories.GroupsRepository
import com.example.trip.usecases.group.GetGroupsUseCase
import com.example.trip.utils.SharedPreferencesHelper
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.every
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
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class GetGroupsUseCaseTest {

    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    private lateinit var useCase: GetGroupsUseCase
    private val groupsRepository: GroupsRepository = mockk()
    private val preferencesHelper: SharedPreferencesHelper = mockk()
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
        useCase = GetGroupsUseCase(groupsRepository, preferencesHelper)
        every { preferencesHelper.getUserId() } returns 1L
    }

    @Test
    fun `verify day plan correctly mapped`() = scope.runTest {
        //given
        coEvery { groupsRepository.getGroups(any()) } returns listOf(groupDto)
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
        val result = useCase()
        runCurrent()

        //then
        assertEquals(Resource.Loading<List<Accommodation>>(), result.first())
        assertEquals(Resource.Success(listOf(group)), result.last())
    }

    @Test
    fun `verify result failure Http`() = scope.runTest {
        //given
        val error = """{"message":"error", "status": BAD_REQUEST, "timestamp": "..."}"""
        coEvery { groupsRepository.getGroups(any()) } throws HttpException(
            Response.error<List<*>>(
                404,
                error.toResponseBody()
            )
        )

        //when
        val result = useCase()
        runCurrent()

        //then
        assertEquals(Resource.Loading<List<*>>(), result.first())
        assertEquals(Resource.Failure<List<*>>(404), result.last())
    }

    @Test
    fun `verify result failure other`() = scope.runTest {
        //given
        coEvery { groupsRepository.getGroups(any()) } throws Exception()

        //when
        val result = useCase()
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