package com.example.wallpaperapplication.wallpaperapi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


 object RetrofitBuilder {

    val base_url = "https://api.pexels.com/v1/"

    fun getInstance(): Retrofit = Retrofit.Builder().baseUrl(base_url)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun provideApiService(retrofit: Retrofit): WallpaperList =
        retrofit.create(WallpaperList::class.java)

}