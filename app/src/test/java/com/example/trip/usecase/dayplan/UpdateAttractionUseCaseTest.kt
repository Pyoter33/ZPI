package com.example.trip.usecase.dayplan

import com.example.trip.dto.AttractionDto
import com.example.trip.models.Resource
import com.example.trip.repositories.DayPlansRepository
import com.example.trip.usecases.dayplan.UpdateAttractionUseCase
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
class UpdateAttractionUseCaseTest {

    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    private lateinit var useCase: UpdateAttractionUseCase
    private val dayPlansRepository: DayPlansRepository = mockk()
    private val attractionDto = mockkClass(AttractionDto::class)

    @Before
    fun setUp() {
        useCase = UpdateAttractionUseCase(dayPlansRepository)
    }

    @Test
    fun `verify vote correctly posted`() = scope.runTest {
        //given
        coEvery { dayPlansRepository.updateAttraction(any()) } returns Unit

        //when
        val result = useCase(attractionDto)
        runCurrent()

        //then
        assertEquals(Resource.Success(Unit), result)
    }

    @Test
    fun `verify result failure Http`() = scope.runTest {
        //given
        coEvery { dayPlansRepository.updateAttraction(any()) } throws HttpException(Response.error<List<*>>(403, "".toResponseBody()))

        //when
        val result = useCase(attractionDto)
        runCurrent()

        //then
        assertEquals(Resource.Failure<List<*>>(403), result)
    }

    @Test
    fun `verify result failure other`() = scope.runTest {
        //given
        coEvery { dayPlansRepository.updateAttraction(any()) } throws Exception()

        //when
        val result = useCase(attractionDto)
        runCurrent()

        //then
        assertEquals(Resource.Failure<List<*>>(0), result)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

}