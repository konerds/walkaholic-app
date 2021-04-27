package com.mapo.walkaholic.ui

import android.app.Activity
import android.content.Intent
import android.text.format.DateUtils
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.ui.auth.LoginFragment
import com.mapo.walkaholic.ui.base.BaseFragment
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

fun <A : Activity> Activity.startNewActivity(activity: Class<A>) {
    Intent(this, activity).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)
    }
}

fun View.visible(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}

fun View.snackbar(message: String, action: (() -> Unit)? = null) {
    val snackbar = Snackbar.make(this, message, Snackbar.LENGTH_LONG)
    action?.let {
        snackbar.setAction("재시도") {
            it()
        }
    }
    snackbar.show()
}

fun Fragment.handleApiError(
    failure: Resource.Failure,
    retry: (() -> Unit) ?= null
) {
    when{
        failure.isNetworkError -> {
            //val errCode = JSONObject(failure.errorCode.toString())
            //val errBody = JSONObject(failure.errorBody?.string())
            requireView().snackbar("네트워크 연결을 확인해주세요\n${failure.errorBody?.string()}", retry)
        }
        failure.errorCode == 401 -> {
            if(this is LoginFragment) {
                requireView().snackbar("로그인할 수 없습니다")
            } else {
                (this as BaseFragment<*, *, *>).logout()
            }
        }
        else -> {
            val error = failure.errorBody?.string().toString()
            requireView().snackbar(error)
        }
    }
}