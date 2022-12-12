package com.example.wallpaperapiapplication.fragments


import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnScrollChangedListener
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.wallpaperapiapplication.R
import com.example.wallpaperapiapplication.databinding.FragmentWallpaperBinding
import com.example.wallpaperapiapplication.utils.Utilities
import com.example.wallpaperapiapplication.viewmodels.WallpaperViewModel
import com.example.wallpaperapplication.adapters.WallpaperAdapter
import com.example.wallpaperapplication.wallpaperapi.PhotoModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.io.File


@AndroidEntryPoint
class WallpaperFragment : Fragment(), WallpaperAdapter.WallpaperOptions {

    lateinit var binding: FragmentWallpaperBinding
    lateinit var fragcontext: Context
    lateinit var list: ArrayList<PhotoModel>
    lateinit var walladapter: WallpaperAdapter
    lateinit var utilities: Utilities
    lateinit var path: String
    private var pageCount = 1
    lateinit var query: String

    private val viewModel: WallpaperViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_wallpaper, container, false)
        fragcontext = binding.root.context

        inits()
        initsUI()
        return binding.root
    }


    fun inits() {

        utilities = Utilities(fragcontext)
        list = arrayListOf()

        //Default wallpapers list
        walladapter = WallpaperAdapter(fragcontext, list, this)
        fetchData(++pageCount)


        binding.wallpaperRecycler.apply {
            adapter = walladapter
            layoutManager = GridLayoutManager(fragcontext, 2)
        }

        //Pagination
        setupPagination(true)


        //Receiver for internet status
        object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                when (p1?.getStringExtra("network")) {
                    "online" -> {
                        binding.apply {
                            list.clear()
                            scrollView.visibility = View.VISIBLE
                            noConnectionLay.visibility = View.GONE
                            progress.visibility = View.VISIBLE
                            fetchData(++pageCount)
                        }
                    }
                    "offline" -> {
                        binding.apply {
                            progress.visibility = View.GONE
                            scrollView.visibility = View.GONE
                            noConnectionLay.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }.apply {
            fragcontext.registerReceiver(
                this,
                IntentFilter("com.example.wallpaperapiapplication.fragments.wallpaperfragment")
            )
        }
    }

    //UI based functions
    private fun initsUI() {
        binding.apply {

            //SerarchView
            searchview.apply {
                isIconified = false
                isEnabled = true
                setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                    androidx.appcompat.widget.SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        fetchSearchedData(++pageCount, query!!)
                        return true
                    }

                    override fun onQueryTextChange(p0: String?): Boolean {
                        return true
                    }
                })
            }

            //Search tiles
            natureSearch.setOnClickListener {
                fetchSearchedData(++pageCount, natureSearch.text.toString())
            }
            advSearch.setOnClickListener {
                fetchSearchedData(++pageCount, advSearch.text.toString())
            }
            archSearch.setOnClickListener {
                fetchSearchedData(++pageCount, archSearch.text.toString())
            }
            carsSearch.setOnClickListener {
                fetchSearchedData(++pageCount, carsSearch.text.toString())
            }
            animalsSearch.setOnClickListener {
                fetchSearchedData(++pageCount, animalsSearch.text.toString())
            }
        }
    }


    //Pagination
    private fun setupPagination(paginate: Boolean) {
        if (paginate) {
            binding.scrollView.apply {
                getViewTreeObserver().addOnScrollChangedListener(
                    OnScrollChangedListener {
                        if (getScrollY() == getChildAt(0).measuredHeight - measuredHeight) {
                            if (query != null && !query.isEmpty())
                                fetchSearchedData(++pageCount, query)
                            else
                                fetchData(++pageCount)
                        }
                    })
            }
        }
    }


    //Default wallpapers
    private fun fetchData(page: Int) {
        list.clear()
        GlobalScope.launch(Dispatchers.Main) {
            try {
                viewModel.getDefaultWallpapers(page, 88).observe(viewLifecycleOwner, Observer {
                    binding.progress.visibility = View.GONE
                    list.addAll(it!!)
                    walladapter.notifyDataSetChanged()
                })
            } catch (ce: CancellationException) {
                throw ce
            } catch (e: Exception) {
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }


    //Searched wallpapers
    private fun fetchSearchedData(page: Int, query: String) {
        list.clear()
        this.query = query

        GlobalScope.launch(Dispatchers.Main) {
            try {
                viewModel.getSearchedWallpapers(query!!, page, 88).observe(viewLifecycleOwner,
                    Observer {
                        if (it.isEmpty()) {
                            binding.apply {
                                noResultsText.visibility = View.VISIBLE
                                scrollView.visibility = View.GONE
                            }
                        } else {
                            binding.apply {
                                progress.visibility = View.GONE
                                noResultsText.visibility = View.GONE
                                scrollView.visibility = View.VISIBLE
                            }
                            list.addAll(it)
                            walladapter.notifyDataSetChanged()
                        }
                    })
            } catch (ce: CancellationException) {
                throw ce
            } catch (e: Exception) {
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }


    //Callback to view wallpaper
    override fun viewWallpaper(photoModel: PhotoModel) {
        utilities.showWallpaper(photoModel, wallpaperFile = null)
    }


    //Callback for downloaded wallpaper
    @SuppressLint("SdCardPath")
    override fun download(photoModel: PhotoModel) {
        utilities.downloadWallpaper(photoModel.src?.portrait!!)
        if (File("/sdcard/DCIM/WallpaperApiApplication").exists())
            path = "/sdcard/DCIM/WallpaperApiApplication"
        else
            path = "/sdcard/DCIM/"
        Utilities(requireActivity()).DirectoryFileObserver(path).startWatching()
    }


    //Callback to set wallpaper
    override fun setWallpaper(photoModel: PhotoModel) {
        utilities.setWallpaper(file = null, photoModel)
    }


}