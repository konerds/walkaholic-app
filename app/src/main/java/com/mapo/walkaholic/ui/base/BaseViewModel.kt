package com.mapo.walkaholic.ui.base

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {
    val progressBarVisibility = ObservableBoolean(false)
    private val _onClickEvent = MutableLiveData<Event<String>>()
    val onClickEvent: LiveData<Event<String>>
        get() = _onClickEvent

    fun onClickEvent(name: String) {
        progressBarVisibility.set(true)
        viewModelScope.launch {
            _onClickEvent.value = Event(name)
            progressBarVisibility.set(false)
        }
    }

    abstract fun init()
}