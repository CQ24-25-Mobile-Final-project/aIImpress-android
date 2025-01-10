package com.hcmus.data

import com.hcmus.presentation.AiGenerateImageViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    single {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    viewModel {
        AiGenerateImageViewModel(get())
    }

    single<ImageRepository> { ImageRepositoryImpl(get()) }


}

