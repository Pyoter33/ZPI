package com.example.trip.service

import com.example.trip.dto.TripGroupPostDto
import com.example.trip.dto.UserDto
import com.example.trip.dto.UserGroupDto
import retrofit2.Response
import retrofit2.http.*

interface TripGroupService {

    @GET("invitation")
    suspend fun getInvitation(
        @Query("group") groupId: Long
    ): Response<Void>

    @PUT("user-group/coordinator")
    suspend fun putCoordinator(
        @Query("groupId") groupId: Long,
        @Query("userId") userId: Long
    ): Response<Void>

    @GET("user-group/coordinators")
    suspend fun getCoordinators(
        @Query("groupId") groupId: Long,
    ): Response<List<UserDto>>

    @GET("trip-group/data")
    suspend fun getGroupById(
        @Query("groupId") groupId: Long
    ): Response<UserGroupDto>

    @GET("trip-group/groups/{userId}")
    suspend fun getGroups(
        @Path("userId") userId: Long
    ): Response<List<UserGroupDto>>

    @POST("trip-group/group")
    suspend fun postGroup(
        @Body tripGroupPostDto: TripGroupPostDto
    ): Response<Void>

    @PATCH("trip-group/group")
    suspend fun updateGroup(
        @Query("groupId") groupId: Long,
        @Body tripGroupPostDto: TripGroupPostDto
    ): Response<Void>

    @DELETE("trip-group/group")
    suspend fun deleteGroup(
        @Query("groupId") groupId: Long
    ): Response<Void>

    @GET("user-group/participants")
    suspend fun getParticipants(
        @Query("groupId") groupId: Long
    ): Response<List<UserDto>>

    @GET("user")
    suspend fun getUser(
        @Query("userId") userId: Long
    ): Response<UserDto>

    @PATCH("user")
    suspend fun updateUser(
        @Query("appUser") appUser: UserDto
    ): Response<Void>

    @DELETE("trip-group/user")
    suspend fun deleteParticipant(
        @Query("groupId") groupId: Long,
        @Query("userId") userId: Long
    ): Response<Void>

    @PUT("trip-group/selected-accommodation")
    suspend fun deleteAcceptedAccommodation(
        @Query("groupId") groupId: Long
    ): Response<Void>

    @PUT("trip-group/selected-availability")
    suspend fun deleteAcceptedAvailability(
        @Query("groupId") groupId: Long
    ): Response<Void>

    @PUT("trip-group")
    suspend fun changeGroupStage(
        @Query("groupId") groupId: Long
    ): Response<Void>

}