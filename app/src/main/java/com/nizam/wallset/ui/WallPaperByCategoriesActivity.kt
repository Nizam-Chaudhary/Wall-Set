package com.nizam.wallset.ui

import android.app.Application
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.nizam.wallset.data.database.WallPaperDatabase
import com.nizam.wallset.data.repositories.WallPaperRepository
import com.nizam.wallset.databinding.ActivityWallPaperByCategoriesBinding
import com.nizam.wallset.ui.adapters.WallPapersRVAdapter

class WallPaperByCategoriesActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityWallPaperByCategoriesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWallPaperByCategoriesBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val database = WallPaperDatabase(this)
        val repository = WallPaperRepository(database)
        val factory = MainViewModelFactory(repository, Application())
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        val adapter = WallPapersRVAdapter(emptyList(), viewModel, this@WallPaperByCategoriesActivity)

        binding.recyclerView.apply {
            this.adapter = adapter
            this.layoutManager = GridLayoutManager(this@WallPaperByCategoriesActivity, 2, GridLayoutManager.VERTICAL, false)
        }

        intent.getStringExtra("category")?.let {category ->
            viewModel.getWallPaperByCategories(category).observe(this) {
                adapter.imageItems = it
                adapter.notifyDataSetChanged()
            }
        }
    }
}