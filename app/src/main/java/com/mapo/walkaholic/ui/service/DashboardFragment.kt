package com.mapo.walkaholic.ui.service

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mapo.walkaholic.data.repository.DashboardRepository
import com.mapo.walkaholic.databinding.FragmentDashboardBinding
import com.mapo.walkaholic.ui.base.BaseFragment

class DashboardFragment : BaseFragment<DashboardViewModel, FragmentDashboardBinding, DashboardRepository>() {
    override fun getViewModel() = DashboardViewModel::class.java

    override fun getFragmentBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ) = FragmentDashboardBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
            DashboardRepository(remoteDataSource.buildApi(Api::class.java), userPreferences)
}