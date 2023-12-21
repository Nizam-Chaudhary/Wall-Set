package com.nizam.wallset.ui

import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nizam.wallset.R
import com.nizam.wallset.data.InternetChecker
import com.nizam.wallset.data.database.SharedPreferences
import com.nizam.wallset.data.database.WallPaperDatabase
import com.nizam.wallset.data.repositories.WallPaperRepository
import com.nizam.wallset.databinding.ActivityMainBinding
import com.nizam.wallset.ui.adapters.MainViewPagerAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                val networkStatus = InternetChecker(this@MainActivity).isNetworkAvailable()

                if (!networkStatus && binding.networkStatus.visibility == View.GONE)
                    withContext(Dispatchers.Main) {
                        binding.networkStatus.text = "No Internet Available"
                        binding.networkStatus.background.setTint(Color.RED)
                        delay(3000L)
                        val fadeIn = ObjectAnimator.ofFloat(binding.networkStatus, "alpha", 0f, 1f)
                        fadeIn.duration = 1000
                        fadeIn.interpolator = AccelerateDecelerateInterpolator()

                        binding.networkStatus.visibility = View.VISIBLE
                        fadeIn.start()
                    }

                if (networkStatus && binding.networkStatus.visibility == View.VISIBLE)
                    withContext(Dispatchers.Main) {
                        binding.networkStatus.text = "Internet Connection Available"
                        binding.networkStatus.background.setTint(Color.GREEN)
                        delay(3000L)
                        val fadeOut = ObjectAnimator.ofFloat(binding.networkStatus, "alpha", 1f, 0f)
                        fadeOut.duration = 2000
                        fadeOut.interpolator = AccelerateDecelerateInterpolator()

                        binding.networkStatus.visibility = View.GONE
                        fadeOut.start()
                    }
                delay(3000L)
            }
        }

        binding.bottomNavigationView.selectedItemId = R.id.home

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
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
                var urlOfJson = ""
                override fun onDataChange(snapshot: DataSnapshot) {
                    urlOfJson = snapshot.value.toString()
                    viewModel.download(urlOfJson, this@MainActivity)
                }

                override fun onCancelled(error: DatabaseError) {
                    viewModel.download(urlOfJson, this@MainActivity)
                }
            })
        }
    }
}
