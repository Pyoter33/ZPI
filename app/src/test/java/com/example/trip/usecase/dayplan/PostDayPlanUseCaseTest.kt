package com.example.trip.usecase.dayplan

import com.example.trip.dto.DayPlanPostDto
import com.example.trip.models.Resource
import com.example.trip.repositories.DayPlansRepository
import com.example.trip.usecases.dayplan.PostDayPlanUseCase
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
class PostDayPlanUseCaseTest {

    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    private lateinit var useCase: PostDayPlanUseCase
    private val dayPlansRepository: DayPlansRepository = mockk()
    private val dayPlanDto = mockkClass(DayPlanPostDto::class)

    @Before
    fun setUp() {
        useCase = PostDayPlanUseCase(dayPlansRepository)
    }

    @Test
    fun `verify day plan correctly posted`() = scope.runTest {
        //given
        coEvery { dayPlansRepository.postDayPlan(any()) } returns Unit

        //when
        val result = useCase(dayPlanDto)
        runCurrent()

        //then
        assertEquals(Resource.Success(Unit), result)
    }

    @Test
    fun `verify result failure Http`() = scope.runTest {
        //given
        val error = """{"message":"error", "status": BAD_REQUEST, "timestamp": "..."}"""
        coEvery { dayPlansRepository.postDayPlan(any()) } throws HttpException(Response.error<List<*>>(403, error.toResponseBody()))

        //when
        val result = useCase(dayPlanDto)
        runCurrent()

        //then
        assertEquals(Resource.Failure<List<*>>(403), result)
    }

    @Test
    fun `verify result failure other`() = scope.runTest {
        //given
        coEvery { dayPlansRepository.postDayPlan(any()) } throws Exception()

        //when
        val result = useCase(dayPlanDto)
        runCurrent()

        //then
        assertEquals(Resource.Failure<List<*>>(0), result)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

}