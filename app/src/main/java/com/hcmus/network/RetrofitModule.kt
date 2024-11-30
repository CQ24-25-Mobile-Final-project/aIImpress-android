package com.hcmus.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.MediaType.get
import retrofit2.Retrofit


@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

  // TODO: add interceptor

  @Singleton
  @Provides
  fun provideDefaultRetrofit(
    json: Json,
    okHttpClient: OkHttpClient,
  ): Retrofit {
    val contentType = "application/json"
    return Retrofit.Builder()
      .baseUrl("http://localhost:8080/")
      .client(okHttpClient)
      .addConverterFactory(json.asConverterFactory(get(contentType)))
      .build()
  }

  @Provides
  fun provideAuthApi(retrofit: Retrofit): OAuthApi = retrofit.create(OAuthApi::class.java)
}