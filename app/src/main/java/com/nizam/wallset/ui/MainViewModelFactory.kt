package com.nizam.wallset.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.nizam.wallset.data.repositories.WallPaperRepository

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(
    private val repository: WallPaperRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return MainViewModel(repository) as T
    }
}