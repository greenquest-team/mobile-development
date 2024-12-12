package com.dicoding.greenquest.data.remote.retrofit

import com.dicoding.greenquest.data.remote.response.LeaderboardResponse
import com.dicoding.greenquest.data.remote.response.LoginResponse
import com.dicoding.greenquest.data.remote.response.MaterialResponse
import com.dicoding.greenquest.data.remote.response.QuestResponse
import com.dicoding.greenquest.data.remote.response.QuizPostResponse
import com.dicoding.greenquest.data.remote.response.QuizResponse
import com.dicoding.greenquest.data.remote.response.RegisterResponse
import com.dicoding.greenquest.data.remote.response.StoryResponse
import com.dicoding.greenquest.data.remote.response.UserPayload
import com.dicoding.greenquest.data.remote.response.UserResponse
import com.dicoding.greenquest.data.remote.response.WasteTypeResponse
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("waste-types")
    suspend fun getWasteTypes(
        @Query("type_name") query: String
    ): WasteTypeResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): LoginResponse

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("tgl_lahir") tglLahir: String,
        @Field("password") password: String
    ): RegisterResponse

    @GET("quests")
    suspend fun getQuest(): QuestResponse

    @GET("leaderboard")
    suspend fun getLeaderboard(): LeaderboardResponse

    @GET("materials")
    suspend fun getMaterial(
        @Query("type_name") query: String
    ): MaterialResponse

    @GET("quizzes")
    suspend fun getQuiz(
        @Query("type_name") query: String
    ): QuizResponse

    @FormUrlEncoded
    @POST("quizzes/submit")
    suspend fun postQuiz(
        @Field("type_name") typeName: String,
        @Field("answers[0]") firstAnswer: String,
        @Field("answers[1]") secondAnswer: String
    ): QuizPostResponse

    @POST("users/{id}")
    suspend fun updateUser(
        @Path("id") userId: Int,
        @Body userPayload: UserPayload
    ): UserResponse

    @FormUrlEncoded
    @POST("users/{id}")
    suspend fun updateUserPoints(
        @Path("id") userId: Int,
        @Field("points") points: String
    ): UserResponse
}