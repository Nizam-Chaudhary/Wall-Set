package com.nizam.wallset.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nizam.wallset.R
import com.nizam.wallset.data.database.SharedPreferences
import com.nizam.wallset.data.database.WallPaperDatabase
import com.nizam.wallset.data.repositories.WallPaperRepository
import com.nizam.wallset.databinding.ActivityMainBinding
import com.nizam.wallset.ui.adapters.MainViewPagerAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        FirebaseApp.initializeApp(this)
        installSplashScreen()

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val database = WallPaperDatabase(this)
        val repository = WallPaperRepository(database)
        val factory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        CoroutineScope(Dispatchers.IO).launch {
            getUrlAndDownloadJSON()
        }

        val viewPagerAdapter = MainViewPagerAdapter(supportFragmentManager, lifecycle)

        binding.vp2Main.apply {
            this.adapter = viewPagerAdapter
            this.currentItem = 1
            this.isUserInputEnabled = false
        }


        binding.bottomNavigationView.selectedItemId = R.id.home

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.home -> binding.vp2Main.currentItem = 1
                R.id.category -> binding.vp2Main.currentItem = 0
                R.id.wallPaper -> binding.vp2Main.currentItem = 2
                R.id.favorites -> binding.vp2Main.currentItem = 3
            }
            return@setOnItemSelectedListener true
        }
    }

    private fun getUrlAndDownloadJSON() {
        val sharedPreferences = SharedPreferences(this)
        val toLoad = sharedPreferences.toLoad(
            arrayOf(
                Calendar.getInstance().get(Calendar.DATE),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.YEAR)
            )
        )
        if(toLoad) {
            val firebaseDatabase = FirebaseDatabase.getInstance()
            val myRef = firebaseDatabase.getReference("jsonUrl")
            myRef.addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    viewModel.download(snapshot.value.toString(), this@MainActivity)
                }

                override fun onCancelled(error: DatabaseError) {
                    viewModel.download("https://ipfs.filebase.io/ipfs/QmRBLTkYv9BfnM18fzJ2hhpKbMWpGPp4aCU2nEMuNcgVn8", this@MainActivity)
                }
            })
        }
    }
}
