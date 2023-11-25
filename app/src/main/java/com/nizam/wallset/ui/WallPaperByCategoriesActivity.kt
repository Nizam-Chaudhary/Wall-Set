package com.nizam.wallset.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nizam.wallset.data.database.WallPaperDatabase
import com.nizam.wallset.data.repositories.WallPaperRepository
import com.nizam.wallset.databinding.ActivityWallPaperByCategoriesBinding
import com.nizam.wallset.ui.adapters.WallPaperRVAdapter

class WallPaperByCategoriesActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityWallPaperByCategoriesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWallPaperByCategoriesBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val database = WallPaperDatabase(this)
        val repository = WallPaperRepository(database)
        val factory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        val adapter = WallPaperRVAdapter(emptyList(), this@WallPaperByCategoriesActivity)

        binding.recyclerView.apply {
            this.adapter = adapter
            this.layoutManager = LinearLayoutManager(this@WallPaperByCategoriesActivity)
        }

        intent.getStringExtra("category")?.let {category ->
            viewModel.getWallPaperByCategories(category).observe(this) {
                adapter.wallpapers = it
                adapter.notifyDataSetChanged()
            }
        }
    }
}