package com.example.wallpaperapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wallpaperapiapplication.R
import com.example.wallpaperapiapplication.databinding.WallpaperCardBinding
import com.example.wallpaperapplication.wallpaperapi.PhotoModel


class WallpaperAdapter(
    var context: Context,
    var list: ArrayList<PhotoModel>,
    var wallpaperOptions: WallpaperOptions
) :
    RecyclerView.Adapter<WallpaperAdapter.WallpaperViewholder>() {

    lateinit var binding :WallpaperCardBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WallpaperViewholder {
       binding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),R.layout.wallpaper_card, parent, false)
        return WallpaperViewholder(binding)
    }


    override fun onBindViewHolder(holder: WallpaperViewholder, position: Int) {
        holder.bind(position)
    }


    override fun getItemCount(): Int {
        return list.size
    }


    inner class WallpaperViewholder(var binding: WallpaperCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                Glide.with(context).load(list.get(position).src?.portrait)
                    .into(imageCard)

                imageCard.setOnClickListener {
                    wallpaperOptions.viewWallpaper(list.get(position))
                }

                downloadButton.setOnClickListener {
                    wallpaperOptions.download(list.get(position))
                }

                setWallpaperButton.setOnClickListener {
                    wallpaperOptions.setWallpaper(list.get(position))
                }
            }

        }
    }

    interface WallpaperOptions {
        fun viewWallpaper(photoModel: PhotoModel)
        fun download(photoModel: PhotoModel)
        fun setWallpaper(photoModel: PhotoModel)
    }
}