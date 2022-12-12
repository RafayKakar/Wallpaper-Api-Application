package com.example.wallpaperapplication.wallpaperapi

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface WallpaperList {
    @Headers("Authorization:563492ad6f917000010000014967936c6e38478cbbaa676a603d047e")
    @GET("curated")
    fun getRandomWallpapers(
        @Query("page")
        page: Int,
        @Query("per_page")
        per_page: Int
    ): Call<SearchModel>

    @Headers("Authorization:563492ad6f917000010000014967936c6e38478cbbaa676a603d047e")
    @GET("search")
     fun queryWallpapers(
        @Query("query")
        query: String,
        @Query("page")
        page: Int,
        @Query("per_page")
        per_page: Int
    ): Call<SearchModel>
}