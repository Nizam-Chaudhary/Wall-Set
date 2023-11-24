package com.nizam.wallset.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nizam.wallset.R
import com.nizam.wallset.data.database.CategoryItem
import com.nizam.wallset.data.database.WallPaperDatabase
import com.nizam.wallset.data.repositories.WallPaperRepository
import com.nizam.wallset.databinding.FragmentCategoriesBinding
import com.nizam.wallset.ui.MainViewModel
import com.nizam.wallset.ui.MainViewModelFactory
import com.nizam.wallset.ui.adapters.CategoryRVAdapter

class CategoriesFragment : Fragment() {

    private lateinit var binding: FragmentCategoriesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_categories, container, false)
        binding = FragmentCategoriesBinding.bind(view)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun onResume() {
        super.onResume()

        val categoryItems = mutableListOf<CategoryItem>()
        val database = WallPaperDatabase(requireContext())
        val repository = WallPaperRepository(database)
        val factory = MainViewModelFactory(repository)
        val viewModel = ViewModelProvider(this,factory)[MainViewModel::class.java]

        val adapter = CategoryRVAdapter(categoryItems, requireContext())
        binding.rvCategory.apply {
            this.layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
        }

        viewModel.getCategories().observe(viewLifecycleOwner, Observer {categories ->
            for(category in categories) {
                viewModel.getDisplayWallForCategories(category).observe(viewLifecycleOwner, Observer {url ->
                    categoryItems.add(CategoryItem(category, url))
                })
            }
            adapter.categoryItems = categoryItems
            adapter.notifyDataSetChanged()
        })
    }
}