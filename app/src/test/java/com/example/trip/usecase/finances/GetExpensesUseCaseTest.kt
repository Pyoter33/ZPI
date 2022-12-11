package com.example.trip.usecase.finances

import com.example.trip.dto.ExpenseDto
import com.example.trip.dto.UserDto
import com.example.trip.models.Expense
import com.example.trip.models.Participant
import com.example.trip.models.Resource
import com.example.trip.models.UserRole
import com.example.trip.repositories.FinancesRepository
import com.example.trip.repositories.ParticipantsRepository
import com.example.trip.usecases.finances.GetExpensesUseCase
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
import java.time.LocalDate
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class GetExpensesUseCaseTest {

    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    private lateinit var useCase: GetExpensesUseCase
    private val financesRepository: FinancesRepository = mockk()
    private val participantsRepository: ParticipantsRepository = mockk()
    private val expenseDto = ExpenseDto(
        1L,
        LocalDateTime.of(2020, 10, 15, 0, 0),
        "",
        BigDecimal.ONE,
        1L,
        1L,
        listOf(1L, 2L)
    )

    @Before
    fun setUp() {
        useCase = GetExpensesUseCase(financesRepository, participantsRepository)
    }

    @Test
    fun `verify expenses correctly mapped`() = scope.runTest {
        //given
        coEvery { financesRepository.getExpenses(any()) } returns listOf(expenseDto)
        coEvery { participantsRepository.getParticipantsForGroup(any()) } returns listOf(
            UserDto(1, "", "1 1", "a", "a"),
            UserDto(2, "", "1 1", "b", "b"),
        )
        val participantCreator = Participant(1, "a a", "", "1 1", UserRole.UNSPECIFIED)
        val participantDebtor = Participant(2, "b b", "", "1 1", UserRole.UNSPECIFIED)
        val expenses = listOf(
            Expense(
                1L,
                1L,
                participantCreator,
                LocalDate.of(2020, 10, 15),
                "",
                BigDecimal.ONE,
                listOf(participantCreator, participantDebtor)
            )
        )

        //when
        val result = useCase(1L)
        runCurrent()

        //then
        assertEquals(Resource.Loading<List<*>>(), result.first())
        assertEquals(Resource.Success(expenses), result.last())
    }

    @Test
    fun `verify result failure Http`() = scope.runTest {
        //given
        val error = """{"message":"error", "status": BAD_REQUEST, "timestamp": "..."}"""
        coEvery { financesRepository.getExpenses(any()) } throws HttpException(
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
        coEvery { financesRepository.getExpenses(any()) } throws Exception()

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