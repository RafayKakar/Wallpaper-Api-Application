package com.example.wallpaperapplication.wallpaperapi

import com.google.gson.annotations.SerializedName

data class SearchModel(
    @SerializedName("photos")
    var photos: ArrayList<PhotoModel>
)
