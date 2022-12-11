package com.example.trip.usecase.dayplan

import com.example.trip.dto.AttractionDto
import com.example.trip.dto.AttractionPlanDto
import com.example.trip.models.Attraction
import com.example.trip.models.Resource
import com.example.trip.repositories.DayPlansRepository
import com.example.trip.usecases.dayplan.GetAttractionsUseCase
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
class GetAttractionsUseCaseTest {

    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    private lateinit var useCase: GetAttractionsUseCase
    private val dayPlansRepository: DayPlansRepository = mockk()
    private val attractionDto = AttractionPlanDto(
        AttractionDto(
            1L,
            "a",
            null,
            "",
            "a",
            "",
            null,
            0.0,
            0.0
        ),
        null
    )

    @Before
    fun setUp() {
        useCase = GetAttractionsUseCase(dayPlansRepository, "")
    }

    @Test
    fun `verify attraction correctly mapped`() = scope.runTest {
        //given
        coEvery { dayPlansRepository.getAttractionsForDayPlan(any()) } returns listOf(attractionDto)
        val attraction =
            Attraction(
                1L,
                1L,
                "a",
                "a",
                null,
                null,
                null,
                "",
                null
            )

        //when
        val result = useCase(1L)
        runCurrent()

        //then
        assertEquals(Resource.Loading<List<*>>(), result.first())
        assertEquals(Resource.Success(listOf(attraction)), result.last())
    }

    @Test
    fun `verify result failure Http`() = scope.runTest {
        //given
        coEvery { dayPlansRepository.getAttractionsForDayPlan(any()) } throws HttpException(Response.error<List<*>>(404, "".toResponseBody()))

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
        coEvery { dayPlansRepository.getAttractionsForDayPlan(any()) } throws Exception()

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