package com.mapo.walkaholic.ui.service

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mapo.walkaholic.data.repository.MapRepository
import com.mapo.walkaholic.databinding.FragmentMapBinding
import com.mapo.walkaholic.ui.base.BaseFragment

class MapFragment : BaseFragment<MapViewModel, FragmentMapBinding, MapRepository>() {
    override fun getViewModel() = MapViewModel::class.java

    override fun getFragmentBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ) = FragmentMapBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
            MapRepository(remoteDataSource.buildApi(Api::class.java), userPreferences)
}