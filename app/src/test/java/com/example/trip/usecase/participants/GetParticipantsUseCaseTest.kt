package com.example.trip.usecase.participants

import com.example.trip.dto.UserDto
import com.example.trip.models.Participant
import com.example.trip.models.Resource
import com.example.trip.models.UserRole
import com.example.trip.repositories.GroupsRepository
import com.example.trip.repositories.ParticipantsRepository
import com.example.trip.usecases.participants.GetParticipantsUseCase
import io.mockk.clearAllMocks
import io.mockk.coEvery
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

@OptIn(ExperimentalCoroutinesApi::class)
class GetParticipantsUseCaseTest {

    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    private lateinit var useCase: GetParticipantsUseCase
    private val participantsRepository: ParticipantsRepository = mockk()
    private val groupsRepository: GroupsRepository = mockk()

    @Before
    fun setUp() {
        useCase = GetParticipantsUseCase(participantsRepository, groupsRepository)
    }

    @Test
    fun `verify participants correctly mapped`() = scope.runTest {
        //given
        coEvery { participantsRepository.getParticipantsForGroup(any()) } returns listOf(
            UserDto(1, "", "1 1", "a", "a")
        )
        coEvery { groupsRepository.getCoordinators(any()) } returns listOf(UserDto(1, "", "1 1", "a", "a"))

        val participant = Participant(1, "a a", "", "1 1", UserRole.COORDINATOR)

        //when
        val result = useCase(1L)
        runCurrent()

        //then
        assertEquals(Resource.Loading<List<*>>(), result.first())
        assertEquals(Resource.Success(listOf(participant)), result.last())
    }

    @Test
    fun `verify result failure Http`() = scope.runTest {
        //given
        val error = """{"message":"error", "status": BAD_REQUEST, "timestamp": "..."}"""
        coEvery { participantsRepository.getParticipantsForGroup(any()) } throws HttpException(
            Response.error<List<*>>(
                404,
                error.toResponseBody()
            )
        )
        coEvery { groupsRepository.getCoordinators(any()) } returns listOf(UserDto(1, "", "1 1", "a", "a"))

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
        coEvery { participantsRepository.getParticipantsForGroup(any()) } throws Exception()
        coEvery { groupsRepository.getCoordinators(any()) } returns listOf(UserDto(1, "", "1 1", "a", "a"))

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