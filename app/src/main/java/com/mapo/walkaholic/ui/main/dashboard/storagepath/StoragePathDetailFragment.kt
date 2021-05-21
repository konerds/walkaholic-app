package com.mapo.walkaholic.ui.main.dashboard.storagepath

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.mapo.walkaholic.data.model.Ranking
import com.mapo.walkaholic.data.model.Theme
import com.mapo.walkaholic.data.network.ApisApi
import com.mapo.walkaholic.data.network.InnerApi
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.network.SgisApi
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.databinding.FragmentDetailStoragePathBinding
import com.mapo.walkaholic.databinding.FragmentDetailThemeBinding
import com.mapo.walkaholic.ui.base.BaseSharedFragment
import com.mapo.walkaholic.ui.base.EventObserver
import com.mapo.walkaholic.ui.base.ViewModelFactory
import com.mapo.walkaholic.ui.handleApiError
import com.mapo.walkaholic.ui.main.challenge.ranking.ChallengeDetailRankingAdapter
import com.mapo.walkaholic.ui.main.challenge.ranking.ChallengeRankingViewPagerAdapter
import com.mapo.walkaholic.ui.main.theme.ThemeDetailAdapter
import com.mapo.walkaholic.ui.main.theme.ThemeViewModel
import com.mapo.walkaholic.ui.snackbar
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class StoragePathDetailFragment(
    private val position: Int
) : BaseSharedFragment<StoragePathViewModel, FragmentDetailStoragePathBinding, MainRepository>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sharedViewModel: StoragePathViewModel by viewModels {
            ViewModelFactory(getFragmentRepository())
        }
        viewModel = sharedViewModel
        viewModel.showToastEvent.observe(
            viewLifecycleOwner,
            EventObserver(this@StoragePathDetailFragment::showToastEvent)
        )

        viewModel.showSnackbarEvent.observe(
            viewLifecycleOwner,
            EventObserver(this@StoragePathDetailFragment::showSnackbarEvent)
        )
        super.onViewCreated(view, savedInstanceState)

        val dummyMission1 =
            Theme(0,"소나무 숲길", "소나무 숲길", "피톤치드", "강북구","90", "1.8")
        val dummyMission2 =
            Theme(0, "경의선 숲길", "경의선 숲길", "피톤치드", "마포구","90", "4.8")
        val dummyArrayList: ArrayList<Theme> = ArrayList()
        dummyArrayList.add(dummyMission1)
        dummyArrayList.add(dummyMission2)

        //사용자 조회
        viewModel.userResponse.observe(viewLifecycleOwner, Observer { _userResponse ->
            when (_userResponse) {
                is Resource.Success -> {
                    //userid와 position으로 해당 종류에 유저가 갖고 있는 목록 조회
                    viewModel.getStoragePath(_userResponse.value.data.first().id, when(position) { 0 -> "00" 1 -> "01" else -> ""})
                    viewModel.storagePathResponse.observe(viewLifecycleOwner, Observer { _storagePathResponse ->
                        when (_storagePathResponse) {
                            is Resource.Success -> {
                                //조회된 코스 목록의 아이디를 이용해 코스 상세 정보 조회
                                //viewModel.getThemeDetail(_storagePathResponse.value.StoragePath.course_id)
                                /*viewModel.themeResponse.observe(viewLifecycleOwner, Observer { _themeResponse ->
                                    binding.storagePathRV.also {
                                        it.layoutManager = LinearLayoutManager(requireContext())
                                        it.setHasFixedSize(true)
                                        when (_themeResponse) {
                                            is Resource.Success -> {
                                                if (!_themeResponse.value.error) {
                                                    it.adapter =
                                                        _themeResponse.value.theme?.let { _themeResponse -> StoragePathDetailAdapter(_themeResponse) }
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

                            }
                            is Resource.Failure -> {
                                _storagePathResponse.errorBody?.let { _storagePathResponse -> Log.e("StoragePath", _storagePathResponse.string()) }
                                handleApiError(_storagePathResponse)
                            }
                        }
                    })
                }
                is Resource.Loading -> {

                }
                is Resource.Failure -> {
                    _userResponse.errorBody?.let { _userResponse -> Log.e("user", _userResponse.string()) }
                    handleApiError(_userResponse)
                }
            }
        })

        val layoutManger = LinearLayoutManager(requireContext())
        layoutManger.orientation = LinearLayoutManager.VERTICAL
        binding.storagePathRV.layoutManager = layoutManger
        binding.storagePathRV.setHasFixedSize(true)

        when (position) {
            0 -> {
                Log.e("냐옹", "냐옹")
                binding.storagePathRV.adapter = StoragePathDetailAdapter(dummyArrayList)
            }
            1 -> {
                binding.storagePathRV.adapter = StoragePathDetailAdapter(dummyArrayList)
            }
        }
    }

    private fun showToastEvent(contents: String) {
        when(contents) {
            null -> { }
            "" -> { }
            else -> {
                Toast.makeText(
                    requireContext(),
                    contents,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showSnackbarEvent(contents: String) {
        when(contents) {
            null -> { }
            "" -> { }
            else -> {
                requireView().snackbar(contents)
            }
        }
    }

    override fun getViewModel() = StoragePathViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentDetailStoragePathBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): MainRepository {
        val jwtToken = runBlocking { userPreferences.jwtToken.first() }
        val api = remoteDataSource.buildRetrofitInnerApi(InnerApi::class.java, jwtToken)
        val apiWeather = remoteDataSource.buildRetrofitApiWeatherAPI(ApisApi::class.java)
        val apiSGIS = remoteDataSource.buildRetrofitApiSGISAPI(SgisApi::class.java)
        return MainRepository(api, apiWeather, apiSGIS, userPreferences)
    }
}


