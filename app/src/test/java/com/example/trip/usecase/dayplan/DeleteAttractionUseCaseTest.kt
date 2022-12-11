package com.example.trip.usecase.dayplan

import com.example.trip.models.Resource
import com.example.trip.repositories.DayPlansRepository
import com.example.trip.usecases.dayplan.DeleteAttractionUseCase
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
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
class DeleteAttractionUseCaseTest {

    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    private lateinit var useCase: DeleteAttractionUseCase
    private val dayPlansRepository: DayPlansRepository = mockk()

    @Before
    fun setUp() {
        useCase = DeleteAttractionUseCase(dayPlansRepository)
    }

    @Test
    fun `verify attraction correctly deleted`() = scope.runTest {
        //given
        coEvery { dayPlansRepository.deleteAttraction(any(), any()) } returns Unit

        //when
        val result = useCase(1L, 1L)
        runCurrent()

        //then
        assertEquals(Resource.Success(Unit), result)
    }

    @Test
    fun `verify result failure Http`() = scope.runTest {
        //given
        coEvery { dayPlansRepository.deleteAttraction(any(), any()) } throws HttpException(Response.error<List<*>>(404, "".toResponseBody()))

        //when
        val result = useCase(1L, 1L)
        runCurrent()

        //then
        assertEquals(Resource.Failure<List<*>>(404), result)
    }

    @Test
    fun `verify result failure other`() = scope.runTest {
        //given
        coEvery { dayPlansRepository.deleteAttraction(any(), any()) } throws Exception()

        //when
        val result = useCase(1L, 1L)
        runCurrent()

        //then
        assertEquals(Resource.Failure<List<*>>(0), result)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }


}