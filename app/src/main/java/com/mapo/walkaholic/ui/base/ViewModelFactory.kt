package com.mapo.walkaholic.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mapo.walkaholic.R
import com.mapo.walkaholic.data.repository.*
import com.mapo.walkaholic.ui.GuideViewModel
import com.mapo.walkaholic.ui.SplashViewModel
import com.mapo.walkaholic.ui.auth.AuthViewModel
import com.mapo.walkaholic.ui.auth.LoginViewModel
import com.mapo.walkaholic.ui.auth.RegisterViewModel
import com.mapo.walkaholic.ui.favoritepath.FavoritePathDetailViewModel
import com.mapo.walkaholic.ui.global.GlobalApplication
import com.mapo.walkaholic.ui.main.MainViewModel
import com.mapo.walkaholic.ui.main.challenge.ChallengeViewModel
import com.mapo.walkaholic.ui.main.dashboard.*
import com.mapo.walkaholic.ui.main.dashboard.calendar.DashboardCalendarViewModel
import com.mapo.walkaholic.ui.main.dashboard.character.info.DashboardCharacterInfoViewModel
import com.mapo.walkaholic.ui.main.dashboard.character.shop.DashboardCharacterShopViewModel
import com.mapo.walkaholic.ui.main.dashboard.profile.DashboardProfileViewModel
import com.mapo.walkaholic.ui.favoritepath.FavoritePathViewModel
import com.mapo.walkaholic.ui.main.challenge.ChallengeDetailViewModel
import com.mapo.walkaholic.ui.main.challenge.ranking.ChallengeDetailRankingViewModel
import com.mapo.walkaholic.ui.main.dashboard.character.info.DashboardCharacterInfoDetailViewModel
import com.mapo.walkaholic.ui.main.dashboard.character.shop.DashboardCharacterShopDetailViewModel
import com.mapo.walkaholic.ui.main.map.MapViewModel
import com.mapo.walkaholic.ui.main.theme.ThemeDetailViewModel
import com.mapo.walkaholic.ui.main.theme.ThemeViewModel
import java.lang.IllegalArgumentException

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val repository: BaseRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        with(modelClass) {
            when {
                isAssignableFrom(SplashViewModel::class.java) -> SplashViewModel(repository as SplashRepository)
                isAssignableFrom(GuideViewModel::class.java) -> GuideViewModel(repository as GuideRepository)
                isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel(repository as AuthRepository)
                isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(repository as AuthRepository)
                isAssignableFrom(RegisterViewModel::class.java) -> RegisterViewModel(repository as AuthRepository)
                isAssignableFrom(MainViewModel::class.java) -> MainViewModel(repository as MainRepository)
                isAssignableFrom(DashboardViewModel::class.java) -> DashboardViewModel(
                    repository as MainRepository
                )
                isAssignableFrom(DashboardProfileViewModel::class.java) -> DashboardProfileViewModel(
                    repository as MainRepository
                )
                isAssignableFrom(DashboardCharacterInfoViewModel::class.java) -> DashboardCharacterInfoViewModel(
                    repository as MainRepository
                )
                isAssignableFrom(DashboardCharacterInfoDetailViewModel::class.java) -> DashboardCharacterInfoDetailViewModel(
                    repository as MainRepository
                )
                isAssignableFrom(DashboardCharacterShopViewModel::class.java) -> DashboardCharacterShopViewModel(
                    repository as MainRepository
                )
                isAssignableFrom(DashboardCharacterShopDetailViewModel::class.java) -> DashboardCharacterShopDetailViewModel(
                    repository as MainRepository
                )
                isAssignableFrom(DashboardCalendarViewModel::class.java) -> DashboardCalendarViewModel(
                    repository as MainRepository
                )
                isAssignableFrom(ThemeViewModel::class.java) -> ThemeViewModel(
                    repository as MainRepository
                )
                isAssignableFrom(ThemeDetailViewModel::class.java) -> ThemeDetailViewModel(
                    repository as MainRepository
                )
                isAssignableFrom(ChallengeViewModel::class.java) -> ChallengeViewModel(
                    repository as MainRepository
                )
                isAssignableFrom(ChallengeDetailViewModel::class.java) -> ChallengeDetailViewModel(
                    repository as MainRepository
                )
                isAssignableFrom(ChallengeDetailRankingViewModel::class.java) -> ChallengeDetailRankingViewModel(
                    repository as MainRepository
                )
                isAssignableFrom(FavoritePathViewModel::class.java) -> FavoritePathViewModel(
                    repository as MainRepository
                )
                isAssignableFrom(FavoritePathDetailViewModel::class.java) -> FavoritePathDetailViewModel(
                    repository as MainRepository
                )
                isAssignableFrom(MapViewModel::class.java) -> MapViewModel(repository as MainRepository)
                else -> throw IllegalArgumentException(
                    GlobalApplication.getGlobalApplicationContext()
                        .getString(R.string.err_unexpected)
                )
            }
        } as T
}