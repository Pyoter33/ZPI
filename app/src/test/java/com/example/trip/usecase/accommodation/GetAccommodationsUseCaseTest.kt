package com.example.trip.usecase.accommodation

import com.example.trip.dto.AccommodationDto
import com.example.trip.dto.AccommodationVoteDto
import com.example.trip.dto.AccommodationVoteId
import com.example.trip.dto.UserGroupDto
import com.example.trip.models.Accommodation
import com.example.trip.models.Resource
import com.example.trip.repositories.AccommodationsRepository
import com.example.trip.repositories.GroupsRepository
import com.example.trip.usecases.accommodation.GetAccommodationsListUseCase
import com.example.trip.utils.SharedPreferencesHelper
import io.mockk.*
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

@OptIn(ExperimentalCoroutinesApi::class)
class GetAccommodationsUseCaseTest {

    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    private lateinit var useCase: GetAccommodationsListUseCase
    private val accommodationsRepository: AccommodationsRepository = mockk()
    private val preferencesHelper: SharedPreferencesHelper = mockk()
    private val groupsRepository: GroupsRepository = mockk()
    private val group = mockkClass(UserGroupDto::class)
    private val accommodationDto = AccommodationDto(
        1L,
        1L,
        1L,
        "",
        "a",
        "b",
        "d",
        "c",
        null,
        "",
        "",
        1,
        BigDecimal.ZERO,
        0.0,
        0.0
    )
    private val votes = listOf(
        AccommodationVoteDto(AccommodationVoteId(1L, 1L))
    )

    @Before
    fun setUp() {
        useCase = GetAccommodationsListUseCase(accommodationsRepository, groupsRepository, preferencesHelper)
        every { group.selectedAccommodationId } returns 0L
    }

    @Test
    fun `verify accommodation correctly mapped voted`() = scope.runTest {
        //given
        coEvery { accommodationsRepository.getAccommodationsList(any()) } returns listOf(accommodationDto)
        coEvery { groupsRepository.getGroup(any()) } returns group
        coEvery { accommodationsRepository.getVotes(any()) } returns votes
        every { preferencesHelper.getUserId() } returns 1L
        val accommodation =
            Accommodation(
                1L,
                1L,
                1L,
                "",
                "a",
                null,
                "",
                "",
                1,
                BigDecimal.ZERO,
                true,
                false
            )

        //when
        val result = useCase(1L)
        runCurrent()

        //then
        assertEquals(Resource.Loading<List<*>>(), result.first())
        assertEquals(Resource.Success(listOf(accommodation)), result.last())
    }

    @Test
    fun `verify accommodation correctly mapped not voted`() = scope.runTest {
        //given
        coEvery { accommodationsRepository.getAccommodationsList(any()) } returns listOf(accommodationDto)
        coEvery { groupsRepository.getGroup(any()) } returns group
        coEvery { accommodationsRepository.getVotes(any()) } returns votes
        every { preferencesHelper.getUserId() } returns 2L
        val accommodation =
            Accommodation(
                1L,
                1L,
                1L,
                "",
                "a",
                null,
                "",
                "",
                1,
                BigDecimal.ZERO,
                false,
                false
            )

        //when
        val result = useCase(1L)
        runCurrent()

        //then
        assertEquals(Resource.Loading<List<*>>(), result.first())
        assertEquals(Resource.Success(listOf(accommodation)), result.last())
    }

    @Test
    fun `verify accommodation correctly mapped accepted`() = scope.runTest {
        //given
        coEvery { accommodationsRepository.getAccommodationsList(any()) } returns listOf(accommodationDto)
        coEvery { groupsRepository.getGroup(any()) } returns group
        every { group.selectedAccommodationId } returns 1L
        coEvery { accommodationsRepository.getVotes(any()) } returns votes
        every { preferencesHelper.getUserId() } returns 2L
        val accommodation =
            Accommodation(
                1L,
                1L,
                1L,
                "",
                "a",
                null,
                "",
                "",
                1,
                BigDecimal.ZERO,
                false,
                true
            )

        //when
        val result = useCase(1L)
        runCurrent()

        //then
        assertEquals(Resource.Loading<List<*>>(), result.first())
        assertEquals(Resource.Success(listOf(accommodation)), result.last())
    }

    @Test
    fun `verify result failure Http`() = scope.runTest {
        //given
        coEvery { accommodationsRepository.getAccommodationsList(any()) } throws HttpException(Response.error<List<*>>(404, "".toResponseBody()))
        coEvery { groupsRepository.getGroup(any()) } returns group
        coEvery { accommodationsRepository.getVotes(any()) } returns votes
        every { preferencesHelper.getUserId() } returns 2L

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
        coEvery { accommodationsRepository.getAccommodationsList(any()) } throws Exception()
        coEvery { groupsRepository.getGroup(any()) } returns group
        coEvery { accommodationsRepository.getVotes(any()) } returns votes
        every { preferencesHelper.getUserId() } returns 2L

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