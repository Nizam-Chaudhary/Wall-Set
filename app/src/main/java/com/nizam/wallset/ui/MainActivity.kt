package com.nizam.wallset.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.nizam.wallset.data.database.WallPaperDatabase
import com.nizam.wallset.data.repositories.WallPaperRepository
import com.nizam.wallset.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val database = WallPaperDatabase(this)
        val repository = WallPaperRepository(database)
        val factory = MainViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        binding.btnDownloadJson.setOnClickListener {
            viewModel.download("https://ipfs.filebase.io/ipfs/QmW9eZLhZT95EP7mUPUNqLZnr2hYq6QBEymnFUP4ruuBjt")
        }
    }
}