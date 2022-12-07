package com.example.trip.usecase.dayplan

import com.example.trip.dto.DayPlanDto
import com.example.trip.models.Accommodation
import com.example.trip.models.DayPlan
import com.example.trip.models.Resource
import com.example.trip.repositories.DayPlansRepository
import com.example.trip.usecases.dayplan.GetDayPlansUseCase
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

@OptIn(ExperimentalCoroutinesApi::class)
class GetDayPlansUseCaseTest {

    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)
   // private val error = {"message":"We cannot find your starting location. Please check or change starting point.","status":"BAD_REQUEST","timestamp":"2022-12-07T19:19:05.23506439+01:00"}


    private lateinit var useCase: GetDayPlansUseCase
    private val dayPlansRepository: DayPlansRepository = mockk()
    private val dayPlanDto = DayPlanDto(
        1L,
        1L,
        LocalDate.of(2020, 10, 15),
        "a",
        0,
        emptySet(),
        null
    )

    @Before
    fun setUp() {
        useCase = GetDayPlansUseCase(dayPlansRepository)
    }

    @Test
    fun `verify day plan correctly mapped`() = scope.runTest {
        //given
        coEvery { dayPlansRepository.getDayPlans(any()) } returns listOf(dayPlanDto)
        val dayPlan =
            DayPlan(
                1L,
                1L,
                "a",
                LocalDate.of(2020, 10, 15),
                0,
                0
            )

        //when
        val result = useCase(1L)
        runCurrent()

        //then
        assertEquals(Resource.Loading<List<Accommodation>>(), result.first())
        assertEquals(Resource.Success(listOf(dayPlan)), result.last())
    }

    @Test
    fun `verify result failure Http`() = scope.runTest {
        //given
        val error = """{"message":"error", "status": BAD_REQUEST, "timestamp": "..."}"""
        coEvery { dayPlansRepository.getDayPlans(any()) } throws HttpException(Response.error<List<*>>(404, error.toResponseBody()))

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
        coEvery { dayPlansRepository.getDayPlans(any()) } throws Exception()

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