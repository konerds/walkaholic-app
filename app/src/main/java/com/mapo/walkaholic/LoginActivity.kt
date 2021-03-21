package com.mapo.walkaholic

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.mapo.walkaholic.databinding.ActivityLoginBinding
import org.json.JSONObject
import java.util.regex.Pattern

@RequiresApi(Build.VERSION_CODES.M)
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        when (Build.VERSION.SDK_INT) {
            in (Build.VERSION_CODES.KITKAT..(Build.VERSION_CODES.M) - 1) -> {
                @Suppress("DEPRECATION")
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            }
            in (Build.VERSION_CODES.M)..Build.VERSION_CODES.R -> {
                @Suppress("DEPRECATION")
                window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                window.statusBarColor = Color.TRANSPARENT
            }
        }
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.loginSignupButton.setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.passwordEdittext.windowToken, 0)
            binding.passwordEdittext.clearFocus()
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        binding.emailLoginButton.setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            val userEmail: String = binding.emailEdittext.text.toString()
            val userPassword: String = binding.passwordEdittext.text.toString()
            when {
                userEmail.isEmpty() || !((Patterns.EMAIL_ADDRESS as Pattern).matcher(userEmail)
                        .matches()) -> {
                    binding.emailEdittext.error =
                            "${getString(R.string.email)}을 ${getString(R.string.input_fail_null)}"
                    binding.emailEdittext.isFocusableInTouchMode = true
                    binding.emailEdittext.requestFocus()
                    imm.showSoftInput(binding.emailEdittext, 0)
                    imm.hideSoftInputFromWindow(binding.emailEdittext.windowToken, 0)
                }
                userPassword.isEmpty() -> {
                    binding.passwordEdittext.error =
                            "${getString(R.string.password)}를 ${getString(R.string.input_fail_null)}"
                    binding.passwordEdittext.isFocusableInTouchMode = true
                    binding.passwordEdittext.requestFocus()
                    imm.showSoftInput(binding.passwordEdittext, 0)
                    imm.hideSoftInputFromWindow(binding.passwordEdittext.windowToken, 0)
                }
                else -> {
                    imm.hideSoftInputFromWindow(binding.passwordEdittext.windowToken, 0)
                    val responseListener: Response.Listener<String?> =
                            Response.Listener { response ->
                                try {
                                    val jsonObject = JSONObject(response)
                                    val success = jsonObject.getBoolean("success")
                                    if (success) {
                                        val userEmail = jsonObject.getString("userEmail")
                                        val userPassword = jsonObject.getString("userPassword")
                                        Toast.makeText(
                                                applicationContext,
                                                "$userEmail ${getString(R.string.signin)} ${getString(R.string.complete)}",
                                                Toast.LENGTH_SHORT
                                        ).show()
                                        val intent =
                                                Intent(this@LoginActivity, MainActivity::class.java)
                                        intent.putExtra("userEmail", userEmail)
                                        intent.putExtra("userPassword", userPassword)
                                        startActivity(intent)
                                    } else {
                                        Toast.makeText(
                                                applicationContext,
                                                "${getString(R.string.signin)} ${getString(R.string.fail)}",
                                                Toast.LENGTH_SHORT
                                        ).show()
                                        return@Listener
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    Toast.makeText(
                                            applicationContext,
                                            "${getString(R.string.error)}",
                                            Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    val loginRequest = LoginRequest(
                            userEmail,
                            userPassword,
                            responseListener
                    )
                    val queue = Volley.newRequestQueue(this@LoginActivity)
                    queue.add(loginRequest)
                }
            }
        }
    }
}