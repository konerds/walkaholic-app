package com.mapo.walkaholic.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mapo.walkaholic.data.repository.*
import com.mapo.walkaholic.ui.auth.AuthViewModel
import com.mapo.walkaholic.ui.service.*
import java.lang.IllegalArgumentException

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
        private val repository: BaseRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel(repository as AuthRepository) as T
            modelClass.isAssignableFrom(DashboardViewModel::class.java) -> DashboardViewModel(repository as DashboardRepository) as T
            modelClass.isAssignableFrom(ThemeViewModel::class.java) -> ThemeViewModel(repository as ThemeRepository) as T
            modelClass.isAssignableFrom(ChallengeViewModel::class.java) -> ChallengeViewModel(repository as ChallengeRepository) as T
            modelClass.isAssignableFrom(MapViewModel::class.java) -> MapViewModel(repository as MapRepository) as T
            else -> throw IllegalArgumentException("ViewModel Class Not Found")
        }
    }
}