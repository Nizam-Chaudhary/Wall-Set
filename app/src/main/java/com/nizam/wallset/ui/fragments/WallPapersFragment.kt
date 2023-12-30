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
import com.nizam.wallset.databinding.FragmentWallPapersBinding
import com.nizam.wallset.ui.MainViewModel
import com.nizam.wallset.ui.MainViewModelFactory
import com.nizam.wallset.ui.adapters.WallPapersRVAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WallPapersFragment : Fragment() {

    private lateinit var binding: FragmentWallPapersBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var wallPapersAdapter: WallPapersRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_wall_papers, container, false)
        binding = FragmentWallPapersBinding.bind(view)
        val database = WallPaperDatabase(requireContext())
        val repository = WallPaperRepository(database)
        val factory = MainViewModelFactory(repository, Application(), requireContext())
        viewModel = ViewModelProvider(this,factory)[MainViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wallPapersAdapter = WallPapersRVAdapter(emptyList(), viewModel, requireContext())

        binding.rvWallPapers.apply {
            this.layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            this.adapter = wallPapersAdapter
        }

        viewModel.getAllImages().observe(viewLifecycleOwner) {
            wallPapersAdapter.imageItems = it
            wallPapersAdapter.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()

        syncFavoritesStatus()
    }

    private fun syncFavoritesStatus() {
        CoroutineScope(Dispatchers.IO).launch {
            if (WallPapersRVAdapter.isChangedFromWallPaper) {
                for (item in WallPapersRVAdapter.urlChangedListFromWallPaper) {
                    withContext(Dispatchers.Main) {
                        wallPapersAdapter.notifyItemChanged(
                            wallPapersAdapter.imageItems.indexOf(
                                item
                            )
                        )
                    }
                    //WallPapersRVAdapter.urlChangedListFromWallPaper.remove(item)
                }
                WallPapersRVAdapter.isChangedFromWallPaper = false
            }
        }
    }
}