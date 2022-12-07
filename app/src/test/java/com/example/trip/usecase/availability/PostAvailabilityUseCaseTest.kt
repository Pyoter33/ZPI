package com.example.trip.usecase.availability

import com.example.trip.dto.AvailabilityPostDto
import com.example.trip.models.Resource
import com.example.trip.repositories.AvailabilityRepository
import com.example.trip.usecases.availability.PostAvailabilityUseCase
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
class PostAvailabilityUseCaseTest {

    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    private lateinit var useCase: PostAvailabilityUseCase
    private val availabilityRepository: AvailabilityRepository = mockk()
    private val availabilityDto = mockkClass(AvailabilityPostDto::class)

    @Before
    fun setUp() {
        useCase = PostAvailabilityUseCase(availabilityRepository)
    }

    @Test
    fun `verify vote correctly posted`() = scope.runTest {
        //given
        coEvery { availabilityRepository.postAvailability(any()) } returns Unit

        //when
        val result = useCase(availabilityDto)
        runCurrent()

        //then
        assertEquals(Resource.Success(Unit), result)
    }

    @Test
    fun `verify result failure Http`() = scope.runTest {
        //given
        coEvery { availabilityRepository.postAvailability(any()) } throws HttpException(Response.error<List<*>>(404, "".toResponseBody()))

        //when
        val result = useCase(availabilityDto)
        runCurrent()

        //then
        assertEquals(Resource.Failure<List<*>>(404), result)
    }

    @Test
    fun `verify result failure other`() = scope.runTest {
        //given
        coEvery { availabilityRepository.postAvailability(any()) } throws Exception()

        //when
        val result = useCase(availabilityDto)
        runCurrent()

        //then
        assertEquals(Resource.Failure<List<*>>(0), result)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }


}