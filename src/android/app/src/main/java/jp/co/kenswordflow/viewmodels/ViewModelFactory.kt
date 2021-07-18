package jp.co.kenswordflow.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        with(modelClass) {
            return when {
                isAssignableFrom(MainActivityViewModel::class.java) -> MainActivityViewModel() as T
                else -> throw IllegalArgumentException("unknown ViewModel")
            }
        }
    }
}
