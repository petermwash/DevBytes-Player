package com.pemwa.devbytesplayer.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * ViewModelFactory for constructing DevByteViewModel with parameter
 */
class DevByteViewModelFactory(
    val app: Application
) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DevByteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DevByteViewModel(app) as T
        }
        throw IllegalArgumentException("Unable to construct viewmodel")
    }
}