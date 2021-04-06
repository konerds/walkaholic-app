package com.mapo.walkaholic.ui.service

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.data.repository.BaseRepository
import com.mapo.walkaholic.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class ChallengeViewModel(
        private val repository: BaseRepository
) : BaseViewModel() {
    override fun init() {

    }

    private val _anyResponse: MutableLiveData<Resource<Unit>> = MutableLiveData()
    val anyResponse: LiveData<Resource<Unit>>
        get() = _anyResponse

    fun anyService(
            par1: String,
            par2: String
    ) = viewModelScope.launch {
        // @TODO For Challenge Service
    }
}