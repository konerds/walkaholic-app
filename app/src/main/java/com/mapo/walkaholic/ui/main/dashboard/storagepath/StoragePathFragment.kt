package com.mapo.walkaholic.ui.main.dashboard.storagepath

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mapo.walkaholic.R
import com.mapo.walkaholic.data.network.ApisApi
import com.mapo.walkaholic.data.network.InnerApi
import com.mapo.walkaholic.data.network.SgisApi
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.databinding.FragmentChallengeBinding
import com.mapo.walkaholic.databinding.FragmentStoragePathBinding
import com.mapo.walkaholic.ui.base.BaseFragment
import com.mapo.walkaholic.ui.base.BaseSharedFragment
import com.mapo.walkaholic.ui.base.EventObserver
import com.mapo.walkaholic.ui.base.ViewModelFactory
import com.mapo.walkaholic.ui.main.challenge.ChallengeViewModel
import com.mapo.walkaholic.ui.main.challenge.ChallengeViewPagerAdapter
import com.mapo.walkaholic.ui.main.theme.ThemeViewModel
import com.mapo.walkaholic.ui.snackbar
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class StoragePathFragment : BaseSharedFragment<StoragePathViewModel, FragmentStoragePathBinding, MainRepository>() {
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sharedViewModel : StoragePathViewModel by viewModels {
            ViewModelFactory(getFragmentRepository())
        }
        viewModel = sharedViewModel
        viewModel.showToastEvent.observe(
            viewLifecycleOwner,
            EventObserver(this@StoragePathFragment::showToastEvent)
        )

        viewModel.showSnackbarEvent.observe(
            viewLifecycleOwner,
            EventObserver(this@StoragePathFragment::showSnackbarEvent)
        )

        super.onViewCreated(view, savedInstanceState)

        tabLayout = binding.storagePathTL
        viewPager = binding.storagePathVP

        val adapter = StoragePathViewPagerAdapter(this, 2)
        viewPager.adapter = adapter
        val tabName : ArrayList<String> = arrayListOf()
        tabName.add("테마")
        tabName.add("코스추천")
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabName?.get(position)
        }.attach()
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.currentItem = tab!!.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
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
    ) = FragmentStoragePathBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() : MainRepository {
        val jwtToken = runBlocking { userPreferences.jwtToken.first() }
        val api = remoteDataSource.buildRetrofitInnerApi(InnerApi::class.java, jwtToken)
        val apiWeather = remoteDataSource.buildRetrofitApiWeatherAPI(ApisApi::class.java)
        val apiSGIS = remoteDataSource.buildRetrofitApiSGISAPI(SgisApi::class.java)
        return MainRepository(api, apiWeather, apiSGIS, userPreferences)
    }
}