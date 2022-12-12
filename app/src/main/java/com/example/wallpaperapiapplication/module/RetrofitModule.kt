package com.example.wallpaperapiapplication.module

import com.example.wallpaperapplication.wallpaperapi.WallpaperList
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule{

    val base_url = "https://api.pexels.com/v1/"

    //Retrofit Instance
    @Singleton
    @Provides
    fun getInstance(): Retrofit = Retrofit.Builder().baseUrl(base_url)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    //Wallpaper Api Interface
    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): WallpaperList =
        retrofit.create(WallpaperList::class.java)

}