package com.mapo.walkaholic.ui.favoritepath

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.mapo.walkaholic.data.model.Theme
import com.mapo.walkaholic.data.network.ApisApi
import com.mapo.walkaholic.data.network.InnerApi
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.network.SgisApi
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.databinding.FragmentDetailFavoritePathBinding
import com.mapo.walkaholic.ui.base.BaseFragment
import com.mapo.walkaholic.ui.handleApiError
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class FavoritePathDetailFragment(
    private val position: Int,
    private val listener: FavoritePathClickListener
) : BaseFragment<FavoritePathDetailViewModel, FragmentDetailFavoritePathBinding, MainRepository>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dummyMission1 =
            Theme(0, "소나무 숲길", "소나무 숲길", "피톤치드", "강북구", "90", "1.8")
        val dummyMission2 =
            Theme(0, "경의선 숲길", "경의선 숲길", "피톤치드", "마포구", "90", "4.8")
        val dummyArrayList: ArrayList<Theme> = ArrayList()
        dummyArrayList.add(dummyMission1)
        dummyArrayList.add(dummyMission2)

        //사용자 조회
        viewModel.userResponse.observe(viewLifecycleOwner, Observer { _userResponse ->
            when (_userResponse) {
                is Resource.Success -> {
                    //userid와 position으로 해당 종류에 유저가 갖고 있는 목록 조회
                    viewModel.getFavoritePath(_userResponse.value.data.first().id, position)
                    viewModel.favoritePathResponse.observe(
                        viewLifecycleOwner,
                        Observer { _favoritePathResponse ->
                            when (_favoritePathResponse) {
                                is Resource.Success -> {
                                    //조회된 코스 목록의 아이디를 이용해 코스 상세 정보 조회
                                    //viewModel.getThemeDetail(_favoritePathResponse.value.FavoritePath.course_id)
                                    /*viewModel.themeResponse.observe(viewLifecycleOwner, Observer { _themeResponse ->
                                        binding.favoritePathRV.also {
                                            it.layoutManager = LinearLayoutManager(requireContext())
                                            it.setHasFixedSize(true)
                                            when (_themeResponse) {
                                                is Resource.Success -> {
                                                    if (!_themeResponse.value.error) {
                                                        it.adapter =
                                                            _themeResponse.value.theme?.let { _themeResponse -> FavoritePathDetailAdapter(_themeResponse) }
                                                    }
                                                    Log.e("Theme_Detail", _themeResponse.value.theme.toString())
                                                }
                                                is Resource.Loading -> {

                                                }
                                                is Resource.Failure -> {
                                                    _themeResponse.errorBody?.let { _themeResponse -> Log.e("Theme_Detail", _themeResponse.string()) }
                                                    handleApiError(_themeResponse)
                                                }
                                            }
                                        }
                                    })*/
                                }
                                is Resource.Loading -> {
                                    // Loading
                                }
                                is Resource.Failure -> {
                                    // Network Error
                                    handleApiError(_favoritePathResponse)
                                }
                            }
                        })
                }
                is Resource.Loading -> {
                    // Loading
                }
                is Resource.Failure -> {
                    // Network Error
                    handleApiError(_userResponse) { viewModel.getUser() }
                }
            }
        })

        val layoutManger = LinearLayoutManager(requireContext())
        layoutManger.orientation = LinearLayoutManager.VERTICAL
        binding.favoritePathRV.layoutManager = layoutManger
        binding.favoritePathRV.setHasFixedSize(true)

        when (position) {
            0 -> {
                Log.e("냐옹", "냐옹")
                binding.favoritePathRV.adapter = FavoritePathDetailAdapter(dummyArrayList, listener)
            }
            1 -> {
                binding.favoritePathRV.adapter = FavoritePathDetailAdapter(dummyArrayList, listener)
            }
        }
    }

    override fun getViewModel() = FavoritePathDetailViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentDetailFavoritePathBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): MainRepository {
        val jwtToken = runBlocking { userPreferences.jwtToken.first() }
        val api = remoteDataSource.buildRetrofitInnerApi(InnerApi::class.java, jwtToken)
        val apiWeather = remoteDataSource.buildRetrofitApiWeatherAPI(ApisApi::class.java)
        val apiSGIS = remoteDataSource.buildRetrofitApiSGISAPI(SgisApi::class.java)
        return MainRepository(api, apiWeather, apiSGIS, userPreferences)
    }
}


