package com.example.trip.usecase.participants

import com.example.trip.dto.AppUserDto
import com.example.trip.models.Resource
import com.example.trip.repositories.ParticipantsRepository
import com.example.trip.usecases.participants.UpdateParticipantUseCase
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.mockkClass
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

@OptIn(ExperimentalCoroutinesApi::class)
class UpdateParticipantUseCaseTest {

    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    private lateinit var useCase: UpdateParticipantUseCase
    private val participantsRepository: ParticipantsRepository = mockk()
    private val appUserDto = mockkClass(AppUserDto::class)

    @Before
    fun setUp() {
        useCase = UpdateParticipantUseCase(participantsRepository)
    }

    @Test
    fun `verify participant correctly updated`() = scope.runTest {
        //given
        coEvery { participantsRepository.updateParticipant(any()) } returns Unit

        //when
        val result = useCase(appUserDto)
        runCurrent()

        //then
        assertEquals(Resource.Success(Unit), result)
    }

    @Test
    fun `verify result failure Http`() = scope.runTest {
        //given
        coEvery { participantsRepository.updateParticipant(any()) } throws HttpException(Response.error<List<*>>(403, "".toResponseBody()))

        //when
        val result = useCase(appUserDto)
        runCurrent()

        //then
        assertEquals(Resource.Failure<List<*>>(403), result)
    }

    @Test
    fun `verify result failure other`() = scope.runTest {
        //given
        coEvery { participantsRepository.updateParticipant(any()) } throws Exception()

        //when
        val result = useCase(appUserDto)
        runCurrent()

        //then
        assertEquals(Resource.Failure<List<*>>(0), result)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }


}