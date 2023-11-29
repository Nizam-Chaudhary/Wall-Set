package com.nizam.wallset.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nizam.wallset.R
import com.nizam.wallset.data.database.WallPaperDatabase
import com.nizam.wallset.data.repositories.WallPaperRepository
import com.nizam.wallset.databinding.FragmentTopPicksBinding
import com.nizam.wallset.ui.MainViewModel
import com.nizam.wallset.ui.MainViewModelFactory
import com.nizam.wallset.ui.adapters.RecyclerPagerAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.abs

class TopPicksFragment : Fragment() {

    private lateinit var binding: FragmentTopPicksBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

        val recommendationPagerAdapter = RecyclerPagerAdapter(emptyList(), requireContext())
        binding.vp2Recommendation.apply {
            this.adapter = recommendationPagerAdapter
        }

        var topPickUrls = mutableListOf<String>()

        CoroutineScope(Dispatchers.IO).launch {
            val url = viewModel.getTodayWall()
            topPickUrls = viewModel.getFourTopPicks()
            topPickUrls.add(2, url)
        }

        CoroutineScope(Dispatchers.Main).launch {
            if(topPickUrls.isNotEmpty()) {
                recommendationPagerAdapter.imagesUrl = topPickUrls
                recommendationPagerAdapter.notifyDataSetChanged()
                binding.vp2Recommendation.currentItem = 2
                binding.wormDotsIndicator.attachTo(binding.vp2Recommendation)
            }
        }

        binding.vp2Recommendation.setPageTransformer { page, position ->
            val absPosition = abs(position)

            page.scaleY = 1 - absPosition / 2
        }

        val topPicksAdapter = RecyclerPagerAdapter(emptyList(), requireContext())

        binding.rvTopPicks.apply {
            this.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            this.adapter = topPicksAdapter
        }

        viewModel.getTopPicks().observe(viewLifecycleOwner) {
            topPicksAdapter.imagesUrl = it
            topPicksAdapter.notifyDataSetChanged()
        }

        /*val browseAdapter = RecyclerPagerAdapter(emptyList(), requireContext())

        binding.rvBrowse.apply {
            this.layoutManager = LinearLayoutManager(requireContext())
            this.adapter = browseAdapter
        }

        viewModel.getAllImages().observe(viewLifecycleOwner) {
            browseAdapter.imagesUrl = it
            browseAdapter.notifyDataSetChanged()
        }*/
    }
}