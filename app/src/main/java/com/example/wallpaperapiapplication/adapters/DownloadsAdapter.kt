package com.example.wallpaperapiapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wallpaperapiapplication.R
import com.example.wallpaperapiapplication.databinding.DownloadCardBinding
import com.example.wallpaperapplication.wallpaperapi.PhotoModel
import java.io.File


class DownloadsAdapter(var context: Context, var list: ArrayList<File>,var downloadedWallpaperOptions: DownloadedWallpaperOptions) :
    RecyclerView.Adapter<DownloadsAdapter.DownloadsViewHolder>() {

    lateinit var binding: DownloadCardBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadsViewHolder {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.download_card, parent, false)
        return DownloadsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DownloadsViewHolder, position: Int) {
        holder.bindData(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class DownloadsViewHolder(var binding: DownloadCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindData(position: Int) {
            Glide.with(context).load(list.get(position))
                .into(binding.imageCard)

            binding.apply {
                setwallpaperButton.setOnClickListener {
                    downloadedWallpaperOptions.setWallpaper(list.get(position))
                }
                root.setOnClickListener {
                    downloadedWallpaperOptions.viewWallpaper(list.get(position))
                }
            }

        }
    }

   interface DownloadedWallpaperOptions{
       fun setWallpaper(file: File)
       fun viewWallpaper(wallpaper: File)
   }

}


