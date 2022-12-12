package com.example.wallpaperapiapplication.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wallpaperapiapplication.repository.WallpaperRepository
import com.example.wallpaperapplication.wallpaperapi.PhotoModel
import com.example.wallpaperapplication.wallpaperapi.SearchModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WallpaperViewModel @Inject constructor(
    private val repository: WallpaperRepository
) : ViewModel() {

    var defaulWallpapersList = MutableLiveData<List<PhotoModel>>()
    var queriedWallpapersList = MutableLiveData<List<PhotoModel>>()


    suspend fun getDefaultWallpapers(page: Int, per_page: Int): LiveData<List<PhotoModel>> {
        defaulWallpapersList.value =
            repository.getDefaultWallpapers(page = page, per_page = per_page).body()?.photos
        return defaulWallpapersList
    }

    suspend fun getSearchedWallpapers(
        query: String,
        page: Int,
        per_page: Int
    ): LiveData<List<PhotoModel>> {
        queriedWallpapersList.value =
            repository.getSearchedWallpapers(page = page, per_page = per_page, query = query)
                .body()?.photos
        return queriedWallpapersList
    }
}