package com.example.trip.service

import com.example.trip.dto.TripGroupDto
import com.example.trip.dto.TripGroupPostDto
import com.example.trip.dto.UserDto
import retrofit2.Response
import retrofit2.http.*

interface TripGroupService {

    @GET("invitation")
    suspend fun getInvitation(
        @Query("group") groupId: Long
    ): Response<String>

    @PUT("user-group/coordinator")
    suspend fun postCoordinator(
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
    ): Response<TripGroupDto>

    @GET("trip-group/groups")
    suspend fun getGroups(): Response<List<TripGroupDto>>

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
}