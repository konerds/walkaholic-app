package com.mapo.walkaholic.ui

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.text.Spannable
import android.text.SpannableString
import android.text.format.DateUtils
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import com.mapo.walkaholic.R
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.ui.auth.AuthActivity
import com.mapo.walkaholic.ui.auth.LoginFragment
import com.mapo.walkaholic.ui.base.BaseActivity
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
    val error = failure.errorBody?.string().toString()
    Log.i(
            ContentValues.TAG, "Error : ${error}"
    )
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
            requireView().snackbar(error)
        }
    }
}

fun Activity.handleApiError(
        failure: Resource.Failure,
        retry: (() -> Unit) ?= null
) {
    when{
        failure.isNetworkError -> {
            //val errCode = JSONObject(failure.errorCode.toString())
            //val errBody = JSONObject(failure.errorBody?.string())
            window.decorView.snackbar("네트워크 연결을 확인해주세요\n${failure.errorBody?.string()}", retry)
        }
        failure.errorCode == 401 -> {
            if(true) {

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
    if(span_text.isNullOrEmpty() || span_text == "null") {
        _span_text = "오류"
    } else {
        _span_text = span_text
    }
    if(full_text.isNullOrEmpty() || full_text == "null") {
        _full_text = "오류"
    } else {
        _full_text = full_text
    }
    val firstMatchingIndex = when(_full_text.indexOf(_span_text)) {
        -1 -> 0
        else -> _full_text.indexOf(_span_text)
    }
    val lastMatchingIndex = when(firstMatchingIndex + _span_text.length) {
        -1 -> 1
        else -> firstMatchingIndex + _span_text.length
    }
    val spannable = SpannableString(_full_text)
    spannable.setSpan(ForegroundColorSpan(span_color), firstMatchingIndex, lastMatchingIndex, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    textView.text = spannable
}

@BindingAdapter("app:theme_code")
fun bindDashTheme(imageView:ImageView, theme_code: String) {
    when(theme_code) {
        "00" -> imageView.setImageResource(R.drawable.theme_healing)
        "01" -> imageView.setImageResource(R.drawable.theme_date)
        "02" -> imageView.setImageResource(R.drawable.theme_exercise)
        else -> { }
    }
}

@BindingAdapter("app:setImage")
fun setImageUrl(view: ImageView, imageSrc: String) {
    Glide.with(view.context)
        .load(imageSrc)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(view)
}