package com.nizam.wallset.ui

import android.app.Application
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.nizam.wallset.CATEGORY_NAME
import com.nizam.wallset.data.database.WallPaperDatabase
import com.nizam.wallset.data.repositories.WallPaperRepository
import com.nizam.wallset.databinding.ActivityWallPaperByCategoriesBinding
import com.nizam.wallset.ui.adapters.WallPapersRVAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WallPaperByCategoriesActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityWallPaperByCategoriesBinding
    private lateinit var adapter: WallPapersRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWallPaperByCategoriesBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val database = WallPaperDatabase(this)
        val repository = WallPaperRepository(database)
        val factory = MainViewModelFactory(repository, Application())
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        val adapter =
            WallPapersRVAdapter(emptyList(), viewModel, this@WallPaperByCategoriesActivity)

        binding.recyclerView.apply {
            this.layoutManager = GridLayoutManager(
                this@WallPaperByCategoriesActivity,
                2,
                GridLayoutManager.VERTICAL,
                false
            )
            this.adapter = adapter
        }

        val category = intent.getStringExtra(CATEGORY_NAME)
        category?.let {
            viewModel.getWallPaperByCategories(category).observe(this) {
                adapter.imageItems = it
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        syncFavoritesStatus()
    }

    private fun syncFavoritesStatus() {
        CoroutineScope(Dispatchers.IO).launch {
            if (WallPapersRVAdapter.isChangedFromCategory) {
                for (item in WallPapersRVAdapter.urlChangedListFromCategories) {
                    withContext(Dispatchers.Main) {
                        adapter.notifyItemChanged(adapter.imageItems.indexOf(item))
                    }
                    //WallPapersRVAdapter.urlChangedListFromCategories.remove(item)
                }
                WallPapersRVAdapter.isChangedFromCategory = false
            }
        }
    }
}