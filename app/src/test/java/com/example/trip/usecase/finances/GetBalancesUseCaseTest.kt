package com.example.trip.usecase.finances

import com.example.trip.dto.UserDto
import com.example.trip.models.*
import com.example.trip.repositories.FinancesRepository
import com.example.trip.repositories.ParticipantsRepository
import com.example.trip.usecases.finances.GetBalancesUseCase
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
import java.math.BigDecimal

@OptIn(ExperimentalCoroutinesApi::class)
class GetBalancesUseCaseTest {

    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    private lateinit var useCase: GetBalancesUseCase
    private val financesRepository: FinancesRepository = mockk()
    private val participantsRepository: ParticipantsRepository = mockk()

    @Before
    fun setUp() {
        useCase = GetBalancesUseCase(financesRepository, participantsRepository)
    }

    @Test
    fun `verify balances correctly mapped`() = scope.runTest {
        //given
        coEvery { financesRepository.getBalances(any()) } returns mapOf(1L to BigDecimal.valueOf(20))
        coEvery { participantsRepository.getParticipantsForGroup(any()) } returns listOf(
            UserDto(1, "", "1 1", "a", "a"),
            UserDto(2, "", "1 1", "b", "b"),
        )
        val balances = listOf(
            Balance(Participant(1, "a a", "", "1 1", UserRole.UNSPECIFIED), BigDecimal.valueOf(20), BigDecimal.valueOf(20), BalanceStatus.POSITIVE),
            Balance(Participant(2, "b b", "", "1 1", UserRole.UNSPECIFIED), BigDecimal.ZERO, BigDecimal.valueOf(20), BalanceStatus.NEUTRAL)
        )

        //when
        val result = useCase(1L)
        runCurrent()

        //then
        assertEquals(Resource.Loading<List<*>>(), result.first())
        assertEquals(Resource.Success(balances), result.last())
    }

    @Test
    fun `verify result failure Http`() = scope.runTest {
        //given
        val error = """{"message":"error", "status": BAD_REQUEST, "timestamp": "..."}"""
        coEvery { financesRepository.getBalances(any()) } throws HttpException(Response.error<List<*>>(404, error.toResponseBody()))

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
        coEvery { financesRepository.getBalances(any()) } throws Exception()

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