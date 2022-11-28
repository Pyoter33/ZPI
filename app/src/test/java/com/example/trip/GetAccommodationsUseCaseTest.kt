//package com.example.trip
//
//import com.example.trip.dto.AccommodationDto
//import com.example.trip.dto.AccommodationVoteDto
//import com.example.trip.dto.AccommodationVoteId
//import com.example.trip.models.Accommodation
//import com.example.trip.models.Resource
//import com.example.trip.repositories.AccommodationsRepository
//import com.example.trip.usecases.accommodation.GetAccommodationsListUseCase
//import com.example.trip.utils.SharedPreferencesHelper
//import io.mockk.clearAllMocks
//import io.mockk.coEvery
//import io.mockk.every
//import io.mockk.mockk
//import junit.framework.Assert.assertEquals
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.flow.first
//import kotlinx.coroutines.flow.flowOf
//import kotlinx.coroutines.flow.last
//import kotlinx.coroutines.test.StandardTestDispatcher
//import kotlinx.coroutines.test.TestScope
//import kotlinx.coroutines.test.runCurrent
//import kotlinx.coroutines.test.runTest
//import okhttp3.ResponseBody.Companion.toResponseBody
//import org.junit.After
//import org.junit.Before
//import org.junit.Test
//import retrofit2.HttpException
//import retrofit2.Response
//import java.math.BigDecimal
//
//@OptIn(ExperimentalCoroutinesApi::class)
//class GetAccommodationsUseCaseTest {
//
//    private val dispatcher = StandardTestDispatcher()
//    private val scope = TestScope(dispatcher)
//
//    private lateinit var useCase: GetAccommodationsListUseCase
//    private val accommodationsRepository: AccommodationsRepository = mockk()
//    private val preferencesHelper: SharedPreferencesHelper = mockk()
//    private val accommodationDto = AccommodationDto(
//        1L,
//        1L,
//        1L,
//        "",
//        "a",
//        "b",
//        "d",
//        "c",
//        null,
//        "",
//        "",
//        1,
//        BigDecimal.ZERO,
//        0.0,
//        0.0
//    )
//    private val votes = listOf(
//        AccommodationVoteDto(AccommodationVoteId(1L, 1L))
//    )
//
//    @Before
//    fun setUp() {
//        useCase = GetAccommodationsListUseCase(accommodationsRepository, preferencesHelper)
//    }
//
//
//    @Test
//    fun `verify accommodation correctly mapped voted`() = scope.runTest {
//        //given
//        every { accommodationsRepository.getAccommodationsList(any()) } returns flowOf(
//            listOf(
//                accommodationDto
//            )
//        )
//        coEvery { accommodationsRepository.getVotes(any()) } returns votes
//        every { preferencesHelper.getUserId() } returns 1L
//        val accommodation =
//            Accommodation(
//                1L,
//                1L,
//                1L,
//                "",
//                "a, b, c, d",
//                null,
//                "",
//                "",
//                1,
//                BigDecimal.ZERO,
//                true,
//                false
//            )
//
//        //when
//        val result = useCase(1L)
//        runCurrent()
//
//        //then
//        assertEquals(Resource.Loading<List<Accommodation>>(), result.first())
//        assertEquals(Resource.Success(listOf(accommodation)), result.last())
//    }
//
//    @Test
//    fun `verify accommodation correctly mapped not voted`() = scope.runTest {
//        //given
//        every { accommodationsRepository.getAccommodationsList(any()) } returns flowOf(
//            listOf(
//                accommodationDto
//            )
//        )
//        coEvery { accommodationsRepository.getVotes(any()) } returns votes
//        every { preferencesHelper.getUserId() } returns 2L
//        val accommodation =
//            Accommodation(
//                1L,
//                1L,
//                1L,
//                "",
//                "a, b, c, d",
//                null,
//                "",
//                "",
//                1,
//                BigDecimal.ZERO,
//                false,
//                false
//            )
//
//        //when
//        val result = useCase(1L)
//        runCurrent()
//
//        //then
//        assertEquals(Resource.Loading<List<Accommodation>>(), result.first())
//        assertEquals(Resource.Success(listOf(accommodation)), result.last())
//    }
//
//    @Test
//    fun `verify result failure`() = scope.runTest {
//        //given
//        every { accommodationsRepository.getAccommodationsList(any()) } throws HttpException(Response.error<List<Accommodation>>(404, "".toResponseBody()))
//        coEvery { accommodationsRepository.getVotes(any()) } returns votes
//        every { preferencesHelper.getUserId() } returns 2L
//
//        //when
//        val result = useCase(1L)
//        runCurrent()
//
//        //then
//        assertEquals(Resource.Loading<List<Accommodation>>(), result.first())
//        assertEquals(Resource.Failure<List<Accommodation>>(404), result.last())
//    }
//
//    @After
//    fun tearDown() {
//        clearAllMocks()
//    }
//
//
//}