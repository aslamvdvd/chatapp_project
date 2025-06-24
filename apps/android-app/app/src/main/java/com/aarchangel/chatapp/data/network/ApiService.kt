package com.aarchangel.chatapp.data.network

import com.aarchangel.chatapp.models.user.UserProfile
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("api/v1/users/search")
    suspend fun searchUsers(@Query("query") query: String): List<UserProfile>

    @GET("api/v1/friends/list")
    suspend fun getFriendsList(@Header("Authorization") token: String): List<UserProfile>

    @POST("api/v1/friends/request")
    suspend fun sendFriendRequest(@Header("Authorization") token: String, @Query("receiver_id") receiverId: String)

    @POST("api/v1/friends/accept")
    suspend fun acceptFriendRequest(@Header("Authorization") token: String, @Query("request_id") requestId: String)

    companion object {
        val instance: ApiService by lazy {
            Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/") // For Android emulator
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
} 