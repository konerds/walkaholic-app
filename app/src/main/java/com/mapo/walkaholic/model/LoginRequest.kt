package com.mapo.walkaholic.model

import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest

class LoginRequest(
        userID: String,
        userPassword: String,
        listener: Response.Listener<String?>?
) :
        StringRequest(
                Method.POST,
                URL,
                listener,
                Response.ErrorListener
                { e -> Log.d("error", "[ + ${e.message}]") }
        ) {
    private val map: MutableMap<String, String>

    @Throws(AuthFailureError::class)
    override fun getParams(): Map<String, String> {
        return map
    }

    companion object {
        private const val URL = "http://10.0.2.2/login.php"
    }

    init {
        map = HashMap()
        map["userID"] = userID
        map["userPassword"] = userPassword
    }
}