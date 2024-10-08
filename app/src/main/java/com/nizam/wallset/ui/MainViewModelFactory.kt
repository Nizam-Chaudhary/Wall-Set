package com.nizam.wallset.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.nizam.wallset.data.repositories.WallPaperRepository

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(
    private val repository: WallPaperRepository,
    private val application: Application,
    private val context: Context
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return MainViewModel(repository, application, context) as T
    }
}