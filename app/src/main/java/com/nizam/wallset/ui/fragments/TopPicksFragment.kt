package com.nizam.wallset.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.nizam.wallset.R
import com.nizam.wallset.data.database.WallPaperDatabase
import com.nizam.wallset.data.repositories.WallPaperRepository
import com.nizam.wallset.databinding.FragmentTopPicksBinding
import com.nizam.wallset.ui.MainViewModel
import com.nizam.wallset.ui.MainViewModelFactory
import com.nizam.wallset.ui.adapters.RecommendationPagerAdapter

class TopPicksFragment : Fragment() {

    private lateinit var binding: FragmentTopPicksBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_top_picks, container, false)
        binding = FragmentTopPicksBinding.bind(view)
        val database = WallPaperDatabase(requireContext())
        val repository = WallPaperRepository(database)
        val factory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this,factory)[MainViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recommendationPagerAdapter = RecommendationPagerAdapter(emptyList(), requireContext())
        binding.vp2Recommendation.apply {
            this.adapter = recommendationPagerAdapter
        }

        var todayWall: String? = null

        viewModel.getFourTopPicks().observeOnce(viewLifecycleOwner) {
            recommendationPagerAdapter.imagesUrl = it
            recommendationPagerAdapter.notifyDataSetChanged()
        }

        viewModel.getTodayWall().observe(viewLifecycleOwner) {
            todayWall = it
        }
    }

    private fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, observer: Observer<T>) {
        observe(owner, object : Observer<T> {
            override fun onChanged(t: T) {
                observer.onChanged(t)
                removeObserver(this) // Remove the observer after the first onChanged event
            }
        })
    }
}