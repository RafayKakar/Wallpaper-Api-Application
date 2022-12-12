package com.example.wallpaperapiapplication.repository


import com.example.wallpaperapplication.wallpaperapi.SearchModel
import com.example.wallpaperapplication.wallpaperapi.WallpaperList
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import kotlin.coroutines.resumeWithException


class WallpaperRepository @Inject constructor(
    private val wallpaperList: WallpaperList
) {

    //Get Default Api Wallpapers
    suspend fun getDefaultWallpapers(page: Int, per_page: Int) =
        suspendCancellableCoroutine<Response<SearchModel>> {

            wallpaperList.getRandomWallpapers(page, per_page).enqueue(object :
                Callback<SearchModel> {
                override fun onResponse(
                    call: Call<SearchModel>,
                    response: Response<SearchModel>
                ) {
                    it.resume(response, null)
                }
                override fun onFailure(call: Call<SearchModel>, t: Throwable) {
                    it.resumeWithException(t)
                }
            })
        }

    //Get Queried Api Wallpapers
    suspend fun getSearchedWallpapers(page: Int, per_page: Int, query: String) =
        suspendCancellableCoroutine<Response<SearchModel>> {

            wallpaperList.queryWallpapers(query, page, per_page)
                .enqueue(object : Callback<SearchModel> {
                    override fun onResponse(
                        call: Call<SearchModel>,
                        response: Response<SearchModel>
                    ) {
                        it.resume(response, null)
                    }
                    override fun onFailure(call: Call<SearchModel>, t: Throwable) {
                        it.resumeWithException(t)
                    }
                })
        }

}