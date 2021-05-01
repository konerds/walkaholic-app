package com.mapo.walkaholic.ui

import android.app.Activity
import android.content.ContentValues
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.ui.auth.LoginFragment
import com.mapo.walkaholic.ui.base.BaseFragment
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
        retry: (() -> Unit)? = null
) {
    val error = failure.errorBody?.string().toString()
    Log.i(
            ContentValues.TAG, "Error : ${error}"
    )
    when {
        failure.isNetworkError -> {
            //val errCode = JSONObject(failure.errorCode.toString())
            //val errBody = JSONObject(failure.errorBody?.string())
            if (context != null) {
                val cm = context!!.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
                val nw = cm.activeNetwork
                val networkCapabilities = cm.getNetworkCapabilities(nw)
                if (networkCapabilities != null) {
                    when {
                        //현재 단말기의 연결유무(Wifi, Data 통신)
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                                || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                                || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                                || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> {
                            requireView().snackbar("API 서버의 통신이 원활하지 않습니다\n${failure.errorBody?.string()}", retry)
                        }
                        else -> {
                            requireView().snackbar("네트워크 연결을 확인해주세요\n${failure.errorBody?.string()}", retry)
                        }
                    }
                }
            } else { }
        }
        failure.errorCode == 401 -> {
            if (this is LoginFragment) {
                requireView().snackbar("로그인할 수 없습니다")
            } else {
                (this as BaseFragment<*, *, *>).logout()
            }
        }
        else -> {
            requireView().snackbar(error)
        }
    }
}

fun Activity.handleApiError(
        failure: Resource.Failure,
        retry: (() -> Unit)? = null
) {
    when {
        failure.isNetworkError -> {
            //val errCode = JSONObject(failure.errorCode.toString())
            //val errBody = JSONObject(failure.errorBody?.string())
            window.decorView.snackbar("네트워크 연결을 확인해주세요\n${failure.errorBody?.string()}", retry)
        }
        failure.errorCode == 401 -> {
            if (true) {

            } else {
                (this as BaseFragment<*, *, *>).logout()
            }
        }
        else -> {
            val error = failure.errorBody?.string().toString()
            window.decorView.snackbar(error)
        }
    }
}

@BindingAdapter("app:full_text", "app:span_text", "app:span_color")
fun formatText(textView: TextView, full_text: String?, span_text: String?, span_color: Int) {
    val _full_text: String
    val _span_text: String
    if (span_text.isNullOrEmpty() || span_text == "null") {
        _span_text = "오류"
    } else {
        _span_text = span_text
    }
    if (full_text.isNullOrEmpty() || full_text == "null") {
        _full_text = "오류"
    } else {
        _full_text = full_text
    }
    val firstMatchingIndex = when (_full_text.indexOf(_span_text)) {
        -1 -> 0
        else -> _full_text.indexOf(_span_text)
    }
    val lastMatchingIndex = when (firstMatchingIndex + _span_text.length) {
        -1 -> 1
        else -> firstMatchingIndex + _span_text.length
    }
    val spannable = SpannableString(_full_text)
    spannable.setSpan(ForegroundColorSpan(span_color), firstMatchingIndex, lastMatchingIndex, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    textView.text = spannable
}