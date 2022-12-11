package com.example.trip.repository

import com.example.trip.dto.*
import com.example.trip.repositories.DayPlansRepository
import com.example.trip.service.DayPlanService
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
class DayPlansRepositoryTest {

    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    private lateinit var repository: DayPlansRepository
    private val dayPlanService: DayPlanService = mockk()

    @Before
    fun setUp() {
        repository = DayPlansRepository(dayPlanService)
    }

    @Test
    fun `verify day plans correctly acquired`() = scope.runTest {
        //given
        val value = listOf(mockkClass(DayPlanDto::class))
        val response = Response.success(value)
        coEvery { dayPlanService.getDayPlansForGroup(any()) } returns response

        //when
        val result = repository.getDayPlans(1L)
        runCurrent()

        //then
        assertEquals(value, result)
    }

    @Test(expected = HttpException::class)
    fun `verify day plans acquired error`() = scope.runTest {
        //given
        val response = Response.error<List<DayPlanDto>>(404, "".toResponseBody())
        coEvery { dayPlanService.getDayPlansForGroup(any()) } returns response

        //when
        repository.getDayPlans(1L)
        runCurrent()
    }

    @Test
    fun `verify attractions correctly acquired`() = scope.runTest {
        //given
        val value = listOf(mockkClass(AttractionPlanDto::class))
        val response = Response.success(value)
        coEvery { dayPlanService.getOptimizedAttractions(any()) } returns response

        //when
        val result = repository.getAttractionsForDayPlan(1L)
        runCurrent()

        //then
        assertEquals(value, result)
    }

    @Test(expected = HttpException::class)
    fun `verify attractions acquired error`() = scope.runTest {
        //given
        val response = Response.error<List<AttractionPlanDto>>(404, "".toResponseBody())
        coEvery { dayPlanService.getOptimizedAttractions(any()) } returns response

        //when
        repository.getAttractionsForDayPlan(1L)
        runCurrent()
    }

    @Test
    fun `verify attractions candidates correctly acquired`() = scope.runTest {
        //given
        val value = listOf(mockkClass(AttractionCandidateDto::class))
        val response = Response.success(value)
        coEvery { dayPlanService.findAttractions(any()) } returns response

        //when
        val result = repository.getAttractionsForQuery("")
        runCurrent()

        //then
        assertEquals(value, result)
    }

    @Test(expected = HttpException::class)
    fun `verify attractions candidates acquired error`() = scope.runTest {
        //given
        val response = Response.error<List<AttractionCandidateDto>>(404, "".toResponseBody())
        coEvery { dayPlanService.findAttractions(any()) } returns response

        //when
        repository.getAttractionsForQuery("")
        runCurrent()
    }

    @Test
    fun `verify day plan correctly posted`() = scope.runTest {
        //given
        val response = Response.success(mockkClass(Void::class))
        coEvery { dayPlanService.postDayPlan(any()) } returns response

        //when
        repository.postDayPlan(mockkClass(DayPlanPostDto::class))
        runCurrent()

        //then
        coVerify(exactly = 1) { dayPlanService.postDayPlan(any()) }
    }

    @Test(expected = HttpException::class)
    fun `verify day plan posted error`() = scope.runTest {
        //given
        val response = Response.error<Void>(404, "".toResponseBody())
        coEvery { dayPlanService.postDayPlan(any()) } returns response

        //when
        repository.postDayPlan(mockkClass(DayPlanPostDto::class))
        runCurrent()
    }

    @Test
    fun `verify day plan correctly updated`() = scope.runTest {
        //given
        val response = Response.success(mockkClass(Void::class))
        coEvery { dayPlanService.updateDayPlan(any(), any()) } returns response

        //when
        repository.updateDayPlan(1L, mockkClass(DayPlanPostDto::class))
        runCurrent()

        //then
        coVerify(exactly = 1) { dayPlanService.updateDayPlan(any(), any()) }
    }

    @Test(expected = HttpException::class)
    fun `verify day plan updated error`() = scope.runTest {
        //given
        val response = Response.error<Void>(404, "".toResponseBody())
        coEvery { dayPlanService.updateDayPlan(any(), any()) } returns response

        //when
        repository.updateDayPlan(1L, mockkClass(DayPlanPostDto::class))
        runCurrent()
    }

    @Test
    fun `verify day plan correctly deleted`() = scope.runTest {
        //given
        val response = Response.success(mockkClass(Void::class))
        coEvery { dayPlanService.deleteDayPlan(any()) } returns response

        //when
        repository.deleteDayPlan(1L)
        runCurrent()

        //then
        coVerify(exactly = 1) { dayPlanService.deleteDayPlan(any()) }
    }

    @Test(expected = HttpException::class)
    fun `verify day plan deleted error`() = scope.runTest {
        //given
        val response = Response.error<Void>(404, "".toResponseBody())
        coEvery { dayPlanService.deleteDayPlan(any()) } returns response

        //when
        repository.deleteDayPlan(1L)
        runCurrent()
    }

    @Test
    fun `verify attraction correctly deleted`() = scope.runTest {
        //given
        val response = Response.success(mockkClass(Void::class))
        coEvery { dayPlanService.deleteAttraction(any(), any()) } returns response

        //when
        repository.deleteAttraction(1L, 1L)
        runCurrent()

        //then
        coVerify(exactly = 1) { dayPlanService.deleteAttraction(any(), any()) }
    }

    @Test(expected = HttpException::class)
    fun `verify attraction deleted error`() = scope.runTest {
        //given
        val response = Response.error<Void>(404, "".toResponseBody())
        coEvery { dayPlanService.deleteAttraction(any(), any()) } returns response

        //when
        repository.deleteAttraction(1L, 1L)
        runCurrent()
    }

    @Test
    fun `verify attraction correctly posted`() = scope.runTest {
        //given
        val response = Response.success(mockkClass(Void::class))
        coEvery { dayPlanService.postAttraction(any(), any()) } returns response

        //when
        repository.postAttraction(1L, mockkClass(AttractionCandidateDto::class))
        runCurrent()

        //then
        coVerify(exactly = 1) { dayPlanService.postAttraction(any(), any()) }
    }

    @Test(expected = HttpException::class)
    fun `verify attraction posted error`() = scope.runTest {
        //given
        val response = Response.error<Void>(404, "".toResponseBody())
        coEvery { dayPlanService.postAttraction(any(), any()) } returns response

        //when
        repository.postAttraction(1L, mockkClass(AttractionCandidateDto::class))
        runCurrent()
    }

    @Test
    fun `verify attraction correctly updated`() = scope.runTest {
        //given
        val response = Response.success(mockkClass(Void::class))
        coEvery { dayPlanService.updateAttraction(any()) } returns response

        //when
        repository.updateAttraction(mockkClass(AttractionDto::class))
        runCurrent()

        //then
        coVerify(exactly = 1) { dayPlanService.updateAttraction(any()) }
    }

    @Test(expected = HttpException::class)
    fun `verify attraction updated error`() = scope.runTest {
        //given
        val response = Response.error<Void>(404, "".toResponseBody())
        coEvery { dayPlanService.updateAttraction(any()) } returns response

        //when
        repository.updateAttraction(mockkClass(AttractionDto::class))
        runCurrent()
    }

    @Test
    fun `verify starting point correctly updated`() = scope.runTest {
        //given
        val response = Response.success(mockkClass(Void::class))
        coEvery { dayPlanService.setStartingPoint(any(), any()) } returns response

        //when
        repository.updateStartingPoint(1L, 1L)
        runCurrent()

        //then
        coVerify(exactly = 1) { dayPlanService.setStartingPoint(any(), any()) }
    }

    @Test(expected = HttpException::class)
    fun `verify starting point updated error`() = scope.runTest {
        //given
        val response = Response.error<Void>(404, "".toResponseBody())
        coEvery { dayPlanService.setStartingPoint(any(), any()) } returns response

        //when
        repository.updateStartingPoint(1L, 1L)
        runCurrent()
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

}