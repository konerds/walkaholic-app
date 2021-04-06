package com.mapo.walkaholic.ui.base

import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {
    abstract fun init()
}