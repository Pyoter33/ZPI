package com.example.trip.usecase.participants

import com.example.trip.dto.AppUserDto
import com.example.trip.models.Resource
import com.example.trip.repositories.ParticipantsRepository
import com.example.trip.usecases.participants.GetUserUseCase
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
import java.time.LocalDate
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class GetUserUseCaseTest {

    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    private lateinit var useCase: GetUserUseCase
    private val participantsRepository: ParticipantsRepository = mockk()

    @Before
    fun setUp() {
        useCase = GetUserUseCase(participantsRepository)
    }

    @Test
    fun `verify user correctly retrieved`() = scope.runTest {
        //given
        val user = AppUserDto(
            1L,
            "1 1",
            "",
            "a",
            "a",
            LocalDate.of(2020, 10, 10),
            LocalDateTime.now()
        )
        coEvery { participantsRepository.getUser(any()) } returns user

        //when
        val result = useCase(1L)
        runCurrent()

        //then
        assertEquals(Resource.Loading<List<*>>(), result.first())
        assertEquals(Resource.Success(user), result.last())
    }

    @Test
    fun `verify result failure Http`() = scope.runTest {
        //given
        val error = """{"message":"error", "status": BAD_REQUEST, "timestamp": "..."}"""
        coEvery { participantsRepository.getUser(any()) } throws HttpException(
            Response.error<List<*>>(
                404,
                error.toResponseBody()
            )
        )


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
        coEvery { participantsRepository.getUser(any()) } throws Exception()

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