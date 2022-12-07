package com.example.trip.usecase.finances

import com.example.trip.dto.SettlementDto
import com.example.trip.dto.UserDto
import com.example.trip.models.*
import com.example.trip.repositories.FinancesRepository
import com.example.trip.repositories.ParticipantsRepository
import com.example.trip.usecases.finances.GetSettlementsUseCase
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
import java.math.BigDecimal
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class GetSettlementsUseCaseTest {

    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    private lateinit var useCase: GetSettlementsUseCase
    private val financesRepository: FinancesRepository = mockk()
    private val participantsRepository: ParticipantsRepository = mockk()
    private val preferencesHelper: SharedPreferencesHelper = mockk()
    private val settlementDto = SettlementDto(
        1L,
        LocalDateTime.of(2020, 10, 15, 0, 0),
        SettlementStatus.PENDING,
        BigDecimal.ONE,
        2L,
        1L,
        1L
    )

    @Before
    fun setUp() {
        useCase = GetSettlementsUseCase(financesRepository, participantsRepository, preferencesHelper)
        every { preferencesHelper.getUserId() } returns 1L
    }

    @Test
    fun `verify expenses correctly mapped`() = scope.runTest {
        //given
        coEvery { financesRepository.getSettlements(any(), any()) } returns listOf(settlementDto)
        coEvery { participantsRepository.getParticipantsForGroup(any()) } returns listOf(
            UserDto(1, "", "1 1", "a", "a"),
            UserDto(2, "", "1 1", "b", "b"),
        )
        val participantCreator = Participant(1, "a a", "", "1 1", UserRole.UNSPECIFIED)
        val participantDebtor = Participant(2, "b b", "", "1 1", UserRole.UNSPECIFIED)
        val settlements = listOf(
            Settlement(
                1L,
                1L,
                SettlementStatus.PENDING,
                BigDecimal.ONE,
                participantDebtor,
                participantCreator
            )
        )

        //when
        val result = useCase(1L)
        runCurrent()

        //then
        assertEquals(Resource.Loading<List<*>>(), result.first())
        assertEquals(Resource.Success(settlements), result.last())
    }

    @Test
    fun `verify result failure Http`() = scope.runTest {
        //given
        val error = """{"message":"error", "status": BAD_REQUEST, "timestamp": "..."}"""
        coEvery { financesRepository.getSettlements(any(), any()) } throws HttpException(
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
        coEvery { financesRepository.getSettlements(any(), any()) } throws Exception()

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