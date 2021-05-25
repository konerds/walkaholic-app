package com.mapo.walkaholic.ui.main.dashboard.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.OnBackPressedCallback
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
import com.mapo.walkaholic.ui.confirmDialog
import com.mapo.walkaholic.ui.handleApiError
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class DashboardProfileFragment :
    BaseFragment<DashboardProfileViewModel, FragmentDashboardProfileBinding, MainRepository>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        viewModel.getUser()
        viewModel.userResponse.observe(viewLifecycleOwner, Observer { _userResponse ->
            when (_userResponse) {
                is Resource.Success -> {
                    when (_userResponse.value.code) {
                        "200" -> {
                            binding.user = _userResponse.value.data.first()
                            if (_userResponse.value.data.first().gender == "남") {
                                binding.dashProfileChipMale.isChecked = true
                                binding.dashProfileChipFemale.isChecked = false
                            } else {
                                binding.dashProfileChipFemale.isChecked = true
                                binding.dashProfileChipMale.isChecked = false
                            }
                        }
                        else -> {
                            confirmDialog(
                                _userResponse.value.message,
                                {
                                    viewModel.getUser()
                                },
                                "재시도"
                            )
                            //requireActivity().startNewActivity(AuthActivity::class.java)
                        }
                    }
                }
                is Resource.Loading -> {
                }
                is Resource.Failure -> {
                    handleApiError(_userResponse) { viewModel.getUser() }
                    //requireActivity().startNewActivity(AuthActivity::class.java)
                }
            }
        })
        val heightItems = mutableListOf<Int>()
        for (item in 100..300) {
            heightItems.add(item)
        }
        val heightAdapter = ArrayAdapter(requireContext(), R.layout.item_dashboard_profile_list, heightItems)
        (binding.dashProfileEtHeight as? AutoCompleteTextView)?.setAdapter(heightAdapter)
        val weightItems = mutableListOf<Int>()
        for (item in 10..300) {
            weightItems.add(item)
        }
        val weightAdapter = ArrayAdapter(requireContext(), R.layout.item_dashboard_profile_list, weightItems)
        (binding.dashProfileEtWeight as? AutoCompleteTextView)?.setAdapter(weightAdapter)
        binding.dashProfileBtnComplete.setOnClickListener {
            val navDirection: NavDirections? =
                DashboardProfileFragmentDirections.actionActionBnvDashProfileToActionBnvDash()
            if (navDirection != null) {
                findNavController().navigate(navDirection)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val navDirection: NavDirections? = DashboardProfileFragmentDirections.actionActionBnvDashProfileToActionBnvDash()
                if (navDirection != null) {
                    findNavController().navigate(navDirection)
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
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
        return MainRepository(api, apiWeather, apiSGIS, userPreferences)
    }
}