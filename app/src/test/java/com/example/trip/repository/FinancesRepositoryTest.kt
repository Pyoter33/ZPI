package com.example.trip.repository

import com.example.trip.dto.ExpenseDto
import com.example.trip.dto.ExpensePostDto
import com.example.trip.dto.SettlementDto
import com.example.trip.repositories.FinancesRepository
import com.example.trip.service.FinanceService
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
import java.math.BigDecimal

@OptIn(ExperimentalCoroutinesApi::class)
class FinancesRepositoryTest {

    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    private lateinit var repository: FinancesRepository
    private val financeService: FinanceService = mockk()

    @Before
    fun setUp() {
        repository = FinancesRepository(financeService)
    }

    @Test
    fun `verify expenses correctly acquired`() = scope.runTest {
        //given
        val value = listOf(mockkClass(ExpenseDto::class))
        val response = Response.success(value)
        coEvery { financeService.getExpenses(any()) } returns response

        //when
        val result = repository.getExpenses(1L)
        runCurrent()

        //then
        assertEquals(value, result)
    }

    @Test(expected = HttpException::class)
    fun `verify expenses acquired error`() = scope.runTest {
        //given
        val response = Response.error<List<ExpenseDto>>(404, "".toResponseBody())
        coEvery { financeService.getExpenses(any()) } returns response

        //when
        repository.getExpenses(1L)
        runCurrent()
    }

    @Test
    fun `verify balances correctly acquired`() = scope.runTest {
        //given
        val value = mapOf(1L to BigDecimal.ZERO)
        val response = Response.success(value)
        coEvery { financeService.getBalances(any()) } returns response

        //when
        val result = repository.getBalances(1L)
        runCurrent()

        //then
        assertEquals(value, result)
    }

    @Test(expected = HttpException::class)
    fun `verify balances acquired error`() = scope.runTest {
        //given
        val response = Response.error<Map<Long, BigDecimal>>(404, "".toResponseBody())
        coEvery { financeService.getBalances(any()) } returns response

        //when
        repository.getBalances(1L)
        runCurrent()
    }

    @Test
    fun `verify settlements correctly acquired`() = scope.runTest {
        //given
        val value = listOf(mockkClass(SettlementDto::class))
        val response = Response.success(value)
        coEvery { financeService.getSettlements(any(), any()) } returns response

        //when
        val result = repository.getSettlements(1L, 1L)
        runCurrent()

        //then
        assertEquals(value, result)
    }

    @Test(expected = HttpException::class)
    fun `verify settlements acquired error`() = scope.runTest {
        //given
        val response = Response.error<List<SettlementDto>>(404, "".toResponseBody())
        coEvery { financeService.getSettlements(any(), any()) } returns response

        //when
        repository.getSettlements(1L, 1L)
        runCurrent()
    }

    @Test
    fun `verify expense correctly posted`() = scope.runTest {
        //given
        val response = Response.success(mockkClass(Void::class))
        coEvery { financeService.postExpense(any(), any()) } returns response

        //when
        repository.postExpense(1L, mockkClass(ExpensePostDto::class))
        runCurrent()

        //then
        coVerify(exactly = 1) { financeService.postExpense(any(), any()) }
    }

    @Test(expected = HttpException::class)
    fun `verify expense posted error`() = scope.runTest {
        //given
        val response = Response.error<Void>(404, "".toResponseBody())
        coEvery { financeService.postExpense(any(), any()) } returns response

        //when
        repository.postExpense(1L, mockkClass(ExpensePostDto::class))
        runCurrent()
    }

    @Test
    fun `verify expense correctly updated`() = scope.runTest {
        //given
        val response = Response.success(mockkClass(Void::class))
        coEvery { financeService.updateExpense(any(), any(), any()) } returns response

        //when
        repository.updateExpense(1L, 1L, mockkClass(ExpensePostDto::class))
        runCurrent()

        //then
        coVerify(exactly = 1) { financeService.updateExpense(any(), any(), any()) }
    }

    @Test(expected = HttpException::class)
    fun `verify expense updated error`() = scope.runTest {
        //given
        val response = Response.error<Void>(404, "".toResponseBody())
        coEvery { financeService.updateExpense(any(), any(), any()) } returns response

        //when
        repository.updateExpense(1L, 1L, mockkClass(ExpensePostDto::class))
        runCurrent()
    }

    @Test
    fun `verify expense correctly deleted`() = scope.runTest {
        //given
        val response = Response.success(mockkClass(Void::class))
        coEvery { financeService.deleteExpense(any(), any()) } returns response

        //when
        repository.deleteExpense(1L, 1L)
        runCurrent()

        //then
        coVerify(exactly = 1) { financeService.deleteExpense(any(), any()) }
    }

    @Test(expected = HttpException::class)
    fun `verify expense deleted error`() = scope.runTest {
        //given
        val response = Response.error<Void>(404, "".toResponseBody())
        coEvery { financeService.deleteExpense(any(), any()) } returns response

        //when
        repository.deleteExpense(1L, 1L)
        runCurrent()
    }

    @Test
    fun `verify settlement correctly resolved`() = scope.runTest {
        //given
        val response = Response.success(mockkClass(Void::class))
        coEvery { financeService.resolveSettlement(any(), any()) } returns response

        //when
        repository.resolveSettlement(1L, 1L)
        runCurrent()

        //then
        coVerify(exactly = 1) { financeService.resolveSettlement(any(), any()) }
    }

    @Test(expected = HttpException::class)
    fun `verify settlement resolved error`() = scope.runTest {
        //given
        val response = Response.error<Void>(404, "".toResponseBody())
        coEvery { financeService.resolveSettlement(any(), any()) } returns response

        //when
        repository.resolveSettlement(1L, 1L)
        runCurrent()
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

}