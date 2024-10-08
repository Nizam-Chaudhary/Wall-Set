package com.nizam.wallset.ui.fragments

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.nizam.wallset.R
import com.nizam.wallset.data.database.WallPaperDatabase
import com.nizam.wallset.data.repositories.WallPaperRepository
import com.nizam.wallset.databinding.FragmentTopPicksBinding
import com.nizam.wallset.ui.MainViewModel
import com.nizam.wallset.ui.MainViewModelFactory
import com.nizam.wallset.ui.adapters.RecyclerPagerAdapter
import com.nizam.wallset.ui.adapters.WallPapersRVAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs

class TopPicksFragment : Fragment() {

    private lateinit var binding: FragmentTopPicksBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var topPicksAdapter: WallPapersRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_top_picks, container, false)
        binding = FragmentTopPicksBinding.bind(view)
        val database = WallPaperDatabase(requireContext())
        val repository = WallPaperRepository(database)
        val factory = MainViewModelFactory(repository, Application(), requireContext())
        viewModel = ViewModelProvider(this,factory)[MainViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recommendationPagerAdapter = RecyclerPagerAdapter(emptyList(), requireContext())
        binding.vp2Recommendation.apply {
            this.adapter = recommendationPagerAdapter
        }

        viewModel.getRecommendationWalls().observe(viewLifecycleOwner) {
            recommendationPagerAdapter.imageItems = it
            recommendationPagerAdapter.notifyDataSetChanged()
            binding.wormDotsIndicator.attachTo(binding.vp2Recommendation)
        }

        binding.vp2Recommendation.setPageTransformer { page, position ->
            val absPosition = abs(position)

            page.scaleY = 1 - absPosition / 2
        }

        topPicksAdapter = WallPapersRVAdapter(emptyList(), viewModel, requireContext())

        binding.rvTopPicks.apply {
            this.layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            this.adapter = topPicksAdapter
        }

        viewModel.getTopPicks().observe(viewLifecycleOwner) {
            topPicksAdapter.imageItems = it
            topPicksAdapter.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()

        syncFavoritesStatus()
    }

    private fun syncFavoritesStatus() {
        CoroutineScope(Dispatchers.IO).launch {
            if (WallPapersRVAdapter.isChangedFromTopPicks) {
                for (item in WallPapersRVAdapter.urlChangedListFromTopPicks) {
                    withContext(Dispatchers.Main) {
                        topPicksAdapter.notifyItemChanged(topPicksAdapter.imageItems.indexOf(item))
                    }
                }
                WallPapersRVAdapter.isChangedFromTopPicks = false
            }
        }
    }
}