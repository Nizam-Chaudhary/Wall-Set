package com.nizam.wallset.ui.fragments

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
import com.nizam.wallset.ui.adapters.AllWallPapersRVAdapter

class WallPapersFragment : Fragment() {

    private lateinit var binding: FragmentWallPapersBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_wall_papers, container, false)
        binding = FragmentWallPapersBinding.bind(view)
        val database = WallPaperDatabase(requireContext())
        val repository = WallPaperRepository(database)
        val factory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this,factory)[MainViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val wallPapersAdapter = AllWallPapersRVAdapter(emptyList(), requireContext())

        binding.rvWallPapers.apply {
            this.layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            this.adapter = wallPapersAdapter
        }

        viewModel.getAllImages().observe(viewLifecycleOwner) {
            wallPapersAdapter.imageItems = it
            wallPapersAdapter.notifyDataSetChanged()
        }
    }
}