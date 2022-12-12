package com.example.wallpaperapplication.wallpaperapi

import com.google.gson.annotations.SerializedName

data class PhotoModel(
    @SerializedName("src")
    var src: UrlModel?=null)