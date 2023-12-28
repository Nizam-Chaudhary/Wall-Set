package com.nizam.wallset.ui.fragments

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.nizam.wallset.R
import com.nizam.wallset.data.database.SharedPreferences
import com.nizam.wallset.data.database.WallPaperDatabase
import com.nizam.wallset.data.repositories.WallPaperRepository
import com.nizam.wallset.databinding.FragmentSettingsBinding
import com.nizam.wallset.ui.MainViewModel
import com.nizam.wallset.ui.MainViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        binding = FragmentSettingsBinding.bind(view)
        val database = WallPaperDatabase(requireContext())
        val repository = WallPaperRepository(database)
        val factory = MainViewModelFactory(repository, Application())
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]
        sharedPreferences = SharedPreferences(requireContext())
        setSlideShowToggleStatus()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toggleSlideShow.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.setSlideShowStatus(isChecked)
            if (isChecked) {
                binding.settingSetSlideShowTime.visibility = View.VISIBLE
                viewModel.startWallPaperSlideShow()
            } else {
                binding.settingSetSlideShowTime.visibility = View.GONE
                viewModel.stopWallPaperSlideShow()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        setSlideShowToggleStatus()
    }

    private fun setSlideShowToggleStatus() {
        CoroutineScope(Dispatchers.IO).launch {
            if (viewModel.getFavoritesCount() <= 0) {
                withContext(Dispatchers.Main) {
                    binding.toggleSlideShow.isEnabled = false
                    binding.ivSlideShow.isEnabled = false
                    binding.tvSlideShow.isEnabled = false
                    sharedPreferences.setSlideShowStatus(false)
                    binding.toggleSlideShow.isChecked = false
                    binding.settingSetSlideShowTime.visibility = View.GONE
                    viewModel.stopWallPaperSlideShow()
                }
            } else {
                withContext(Dispatchers.Main) {
                    binding.toggleSlideShow.isEnabled = true
                    binding.ivSlideShow.isEnabled = true
                    binding.tvSlideShow.isEnabled = true
                    binding.toggleSlideShow.isChecked = sharedPreferences.getSlideShowStatus()
                }
            }
            if (binding.toggleSlideShow.isChecked) {
                withContext(Dispatchers.IO) {
                    binding.settingSetSlideShowTime.visibility = View.VISIBLE
                }
            }
        }
    }
}