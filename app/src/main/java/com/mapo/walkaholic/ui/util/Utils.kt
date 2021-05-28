package com.mapo.walkaholic.ui

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.mapo.walkaholic.R
import com.mapo.walkaholic.data.network.Resource
import com.mapo.walkaholic.ui.auth.LoginFragment
import com.mapo.walkaholic.ui.global.GlobalApplication
import kotlinx.android.synthetic.main.dialog_alert.view.*
import kotlinx.android.synthetic.main.dialog_confirm.view.*

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

fun View.toastMessage(message: String) {
    val toast = Toast.makeText(
        context,
        message,
        Toast.LENGTH_SHORT
    )
    toast.show()
}

/*fun View.snackbar(message: String, action: (() -> Unit)? = null) {
    val snackbar = Snackbar.make(this, message, Snackbar.LENGTH_LONG)
    action?.let {
        snackbar.setAction("재시도") {
            it()
        }
    }
    snackbar.show()
}*/

fun Fragment.confirmDialog(message: String, onClickConfirm: (() -> Unit)? = null, confirmText: String?) {
    requireActivity().confirmDialog(message, onClickConfirm, confirmText)
}

fun Fragment.notCancelableConfirmDialog(message: String, onClickConfirm: (() -> Unit), confirmText: String?) {
    requireActivity().confirmDialog(message, onClickConfirm, confirmText)
}

fun Fragment.alertDialog(message: String, onClickNo: (() -> Unit)? = null, onClickYes: () -> Unit) {
    requireActivity().alertDialog(message, onClickNo, onClickYes)
}


fun Activity.confirmDialog(
    message: String,
    onClickConfirm: (() -> Unit)? = null,
    confirmText: String?
) {
    val confirmDialogLayout = LayoutInflater.from(this)
        .inflate(R.layout.dialog_confirm, null, false)
    val materialAlertDialogBuilder = MaterialAlertDialogBuilder(this).setView(
        confirmDialogLayout
    ).create()
    materialAlertDialogBuilder.setOnShowListener { _confirmDialog ->
        confirmDialogLayout.dialogConfirmTv1.text = message
        if (!confirmText.isNullOrEmpty()) {
            confirmDialogLayout.dialogConfirmTv2.text = confirmText
        }
        confirmDialogLayout.dialogConfirmTv2.setOnClickListener {
            if (onClickConfirm != null) {
                onClickConfirm()
            }
            _confirmDialog.dismiss()
        }
    }
    materialAlertDialogBuilder.show()
}

fun Activity.notCancelableConfirmDialog(
    message: String,
    onClickConfirm: (() -> Unit),
    confirmText: String?
) {
    val confirmDialogLayout = LayoutInflater.from(this)
        .inflate(R.layout.dialog_confirm, null, false)
    val materialAlertDialogBuilder = MaterialAlertDialogBuilder(this).setView(
        confirmDialogLayout
    ).setCancelable(false).create()
    materialAlertDialogBuilder.setCanceledOnTouchOutside(false)
    materialAlertDialogBuilder.setOnShowListener { _confirmDialog ->
        confirmDialogLayout.dialogConfirmTv1.text = message
        if (!confirmText.isNullOrEmpty()) {
            confirmDialogLayout.dialogConfirmTv2.text = confirmText
        }
        confirmDialogLayout.dialogConfirmTv2.setOnClickListener {
            onClickConfirm()
            _confirmDialog.dismiss()
        }
    }
    materialAlertDialogBuilder.show()
}

fun Activity.alertDialog(message: String, onClickNo: (() -> Unit)? = null, onClickYes: () -> Unit) {
    val alertDialogLayout = LayoutInflater.from(this)
        .inflate(R.layout.dialog_alert, null, false)
    val materialAlertDialogBuilder = MaterialAlertDialogBuilder(this).setView(
        alertDialogLayout
    ).create()
    materialAlertDialogBuilder.setOnShowListener { _alertDialog ->
        alertDialogLayout.dialogAlertTv1.text = message
        alertDialogLayout.dialogAlertTvNo.setOnClickListener {
            if (onClickNo != null) {
                onClickNo()
            }
            _alertDialog.dismiss()
        }
        alertDialogLayout.dialogAlertTvYes.setOnClickListener {
            _alertDialog.dismiss()
            onClickYes()
        }
    }
    materialAlertDialogBuilder.show()
}


fun Fragment.handleApiError(
    failure: Resource.Failure, action: (() -> Unit)? = null
) {
    requireActivity().handleApiError(failure, action)
}

fun Activity.handleApiError(
    failure: Resource.Failure, action: (() -> Unit)? = null
) {
    val notNullErrorBody =
        if (failure.errorBody != null && failure.errorBody.string() != "null") {
            "\n" + failure.errorBody.string()
        } else {
            ""
        }
    when {
        failure.isNetworkError -> {
            if (!checkNetworkState(window.decorView.context)) {
                notCancelableConfirmDialog(
                    "네트워크 연결 후 재시도해주세요${notNullErrorBody}",
                    action!!,
                    "재시도"
                )
            } else {
                confirmDialog(
                    "API 서버와의 통신이 원활하지 않습니다${notNullErrorBody}",
                    action,
                    "재시도"
                )
            }
        }
        else -> {
            confirmDialog(
                "API 서버와의 통신이 원활하지 않습니다${notNullErrorBody}",
                action,
                "재시도"
            )
        }
    }
}

fun checkNetworkState(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val nw = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
            else -> false
        }
    } else {
        val nwInfo = connectivityManager.activeNetworkInfo ?: return false
        return nwInfo.isConnected
    }
}

@BindingAdapter("app:full_text", "app:span_text", "app:span_color")
fun formatText(textView: TextView, _fullText: String?, _spanText: String?, spanColor: Int) {
    val fullText: String
    val spanText: String
    if (_spanText.isNullOrEmpty() || _spanText == "null") {
        spanText = "오류"
    } else {
        spanText = _spanText
    }
    if (_fullText.isNullOrEmpty() || _fullText == "null") {
        fullText = "오류"
    } else {
        fullText = _fullText
    }
    val firstMatchingIndex = when (fullText.indexOf(spanText)) {
        -1 -> 0
        else -> fullText.indexOf(spanText)
    }
    val lastMatchingIndex = when (firstMatchingIndex + spanText.length) {
        -1 -> 1
        else -> firstMatchingIndex + spanText.length
    }
    val spannable = SpannableString(fullText)
    spannable.setSpan(
        ForegroundColorSpan(spanColor),
        firstMatchingIndex,
        lastMatchingIndex,
        Spannable.SPAN_INCLUSIVE_EXCLUSIVE
    )
    textView.text = spannable
}

@BindingAdapter("app:setImage")
fun setImageUrl(view: ImageView, imageSrc: String?) {
    if (!imageSrc.isNullOrEmpty()) {
        Glide.with(view.context)
            .load("${RESOURCE_BASE_URL}${imageSrc}")
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
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