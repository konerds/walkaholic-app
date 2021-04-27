package com.mapo.walkaholic.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mapo.walkaholic.R
import com.mapo.walkaholic.data.repository.*
import com.mapo.walkaholic.ui.auth.AuthViewModel
import com.mapo.walkaholic.ui.global.GlobalApplication
import com.mapo.walkaholic.ui.main.challenge.ChallengeViewModel
import com.mapo.walkaholic.ui.main.dashboard.DashboardViewModel
import com.mapo.walkaholic.ui.main.map.MapViewModel
import com.mapo.walkaholic.ui.main.theme.ThemeViewModel
import java.lang.IllegalArgumentException

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
        private val repository: BaseRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            with(modelClass) {
                when {
                    isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel(repository as AuthRepository)
                    isAssignableFrom(DashboardViewModel::class.java) -> DashboardViewModel(
                            repository as DashboardRepository
                    )
                    isAssignableFrom(ThemeViewModel::class.java) -> ThemeViewModel(repository as ThemeRepository)
                    isAssignableFrom(ChallengeViewModel::class.java) -> ChallengeViewModel(
                            repository as ChallengeRepository
                    )
                    isAssignableFrom(MapViewModel::class.java) -> MapViewModel(repository as MapRepository)
                    else -> throw IllegalArgumentException(
                            GlobalApplication.getGlobalApplicationContext()
                                    .getString(R.string.err_unexpected)
                    )
                }
            } as T
}