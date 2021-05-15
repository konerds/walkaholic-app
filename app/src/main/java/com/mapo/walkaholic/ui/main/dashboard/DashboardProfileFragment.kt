package com.mapo.walkaholic.ui.main.dashboard

import android.graphics.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.mapo.walkaholic.R
import com.mapo.walkaholic.data.network.ApisApi
import com.mapo.walkaholic.data.network.InnerApi
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.network.SgisApi
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.databinding.FragmentDashboardProfileBinding
import com.mapo.walkaholic.ui.base.BaseFragment
import com.mapo.walkaholic.ui.base.EventObserver
import com.mapo.walkaholic.ui.handleApiError
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.math.*

class DashboardProfileFragment :
    BaseFragment<DashboardProfileViewModel, FragmentDashboardProfileBinding, MainRepository>() {
    companion object {
        private const val PIXELS_PER_METRE = 4
        private const val ANIMATION_DURATION = 300
        private const val CHARACTER_BETWEEN_CIRCLE_PADDING = PIXELS_PER_METRE * 30
        private const val CHARACTER_EXP_CIRCLE_SIZE = PIXELS_PER_METRE * 30
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        viewModel.onClickEvent.observe(
            viewLifecycleOwner,
            EventObserver(this@DashboardProfileFragment::onClickEvent)
        )
        viewModel.userResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    if (!it.value.error) {
                        binding.user = it.value.user
                        if(it.value.user.user_gender == "0") {
                            binding.dashProfileChipMale.isChecked = true
                            binding.dashProfileChipFemale.isChecked = false
                        } else {
                            binding.dashProfileChipFemale.isChecked = true
                            binding.dashProfileChipMale.isChecked = false
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.err_user),
                            Toast.LENGTH_SHORT
                        ).show()
                        //logout()
                        //requireActivity().startNewActivity(AuthActivity::class.java)
                    }
                }
                is Resource.Loading -> {
                }
                is Resource.Failure -> {
                    handleApiError(it)
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.err_user),
                        Toast.LENGTH_SHORT
                    ).show()
                    //logout()
                    //requireActivity().startNewActivity(AuthActivity::class.java)
                }
            }
        })
        viewModel.getUser()
        val heightItems = mutableListOf<Int>()
        for(item in 100..300) {
            heightItems.add(item)
        }
        val heightAdapter = ArrayAdapter(requireContext(), R.layout.item_text_view, heightItems)
        (binding.dashProfileEtHeight as? AutoCompleteTextView)?.setAdapter(heightAdapter)
        val weightItems = mutableListOf<Int>()
        for(item in 10..300) {
            weightItems.add(item)
        }
        val weightAdapter = ArrayAdapter(requireContext(), R.layout.item_text_view, weightItems)
        (binding.dashProfileEtWeight as? AutoCompleteTextView)?.setAdapter(weightAdapter)
    }

    private fun onClickEvent(name: String) {
        val navDirection : NavDirections? =
        when (name) {
            "profile_completed" -> {
                DashboardProfileFragmentDirections.actionActionBnvDashProfileToActionBnvDash()
            }
            else -> { null }
        }
        if(navDirection != null) {
            findNavController().navigate(navDirection)
        }
    }

    override fun getViewModel() = DashboardProfileViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentDashboardProfileBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): MainRepository {
        val jwtToken = runBlocking { userPreferences.jwtToken.first() }
        val api = remoteDataSource.buildRetrofitInnerApi(InnerApi::class.java, jwtToken)
        val apiWeather = remoteDataSource.buildRetrofitApiWeatherAPI(ApisApi::class.java)
        val apiSGIS = remoteDataSource.buildRetrofitApiSGISAPI(SgisApi::class.java)
        return MainRepository.getInstance(api, apiWeather, apiSGIS, userPreferences)
    }
}