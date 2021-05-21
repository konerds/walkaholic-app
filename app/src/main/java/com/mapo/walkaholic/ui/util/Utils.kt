package com.mapo.walkaholic.ui

import android.app.Activity
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.snackbar.Snackbar
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.ui.auth.LoginFragment
import com.mapo.walkaholic.ui.global.GlobalApplication
import kotlin.coroutines.coroutineContext

private const val RESOURCE_BASE_URL = "http://15.164.103.223:8080/static/img/"

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
            requireView().snackbar("네트워크 연결을 확인해주세요\n${failure.errorBody?.string()}", retry)
        }
        failure.errorCode == 401 -> {
            if (this is LoginFragment) {
                requireView().snackbar("로그인할 수 없습니다")
            } else {
                //(this as BaseFragment<*, *, *>).logout()
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
                //(this as BaseFragment<*, *, *>).logout()
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
    spannable.setSpan(
        ForegroundColorSpan(span_color),
        firstMatchingIndex,
        lastMatchingIndex,
        Spannable.SPAN_INCLUSIVE_EXCLUSIVE
    )
    textView.text = spannable
}

@BindingAdapter("app:setImage")
fun setImageUrl(view: ImageView, imageSrc: String?) {
    if(!imageSrc.isNullOrEmpty()) {
        Glide.with(view.context)
            .load("${RESOURCE_BASE_URL}${imageSrc}")
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        resource.minimumWidth.toFloat(),
                        GlobalApplication.getGlobalApplicationContext().resources.displayMetrics
                    ).toInt()
                    view.minimumWidth = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        resource.minimumWidth.toFloat(),
                        GlobalApplication.getGlobalApplicationContext().resources.displayMetrics
                    ).toInt()
                    view.minimumHeight = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        resource.minimumHeight.toFloat(),
                        GlobalApplication.getGlobalApplicationContext().resources.displayMetrics
                    ).toInt()
                    view.setImageDrawable(resource)
                    Log.d(
                        TAG,
                        "${resource.minimumWidth} ${resource.minimumHeight} ${view.minimumWidth} ${view.minimumHeight}"
                    )
                }

                override fun onLoadCleared(placeholder: Drawable?) {}

            })
    }
}