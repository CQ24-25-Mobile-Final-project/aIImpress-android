package com.hcmus.network

import retrofit2.http.*
import com.hcmus.data.model.*
import com.hcmus.network.request.*
import com.hcmus.network.response.*

interface OAuthApi {

  @POST("/api/oauth/access-token")
  suspend fun refreshToken(@Body body: RefreshTokenRequest): AuthToken

  @FormUrlEncoded
  @POST("/login")
  suspend fun login(@Field("username") username: String, @Field("password") pass: String): LoginResponse

  @POST("/users/sign-up")
  suspend fun signUp(@Body body: UserRegisterRequest): UserResponse
}