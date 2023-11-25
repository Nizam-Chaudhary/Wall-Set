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
import com.nizam.wallset.databinding.FragmentCategoriesBinding
import com.nizam.wallset.ui.MainViewModel
import com.nizam.wallset.ui.MainViewModelFactory
import com.nizam.wallset.ui.adapters.CategoryRVAdapter

class CategoriesFragment : Fragment() {

    private lateinit var binding: FragmentCategoriesBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_categories, container, false)
        binding = FragmentCategoriesBinding.bind(view)
        val database = WallPaperDatabase(requireContext())
        val repository = WallPaperRepository(database)
        val factory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this,factory)[MainViewModel::class.java]
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = CategoryRVAdapter(emptyList(), requireContext())
        binding.rvCategory.apply {
            this.layoutManager = GridLayoutManager(requireContext(), 2)
            this.adapter = adapter
        }

        viewModel.getCategoryItems().observe(viewLifecycleOwner) {
            adapter.categoryItems = it
            adapter.notifyDataSetChanged()
        }
    }
}