package com.example.wallpaperapplication.wallpaperapi
import com.google.gson.annotations.SerializedName


data class UrlModel(
    @SerializedName("portrait")
    var portrait  : String? = null)