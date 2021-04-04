package com.mapo.walkaholic.ui.service

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mapo.walkaholic.data.repository.ChallengeRepository
import com.mapo.walkaholic.databinding.FragmentChallengeBinding
import com.mapo.walkaholic.ui.base.BaseFragment

class ChallengeFragment : BaseFragment<ChallengeViewModel, FragmentChallengeBinding, ChallengeRepository>() {
    override fun getViewModel() = ChallengeViewModel::class.java

    override fun getFragmentBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ) = FragmentChallengeBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
            ChallengeRepository(remoteDataSource.buildApi(Api::class.java), userPreferences)
}