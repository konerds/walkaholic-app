package com.mapo.walkaholic.ui.main.dashboard.weather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.Fragment
import com.mapo.walkaholic.R
import com.mapo.walkaholic.databinding.FragmentDashboardWeatherWebViewBinding
import com.mapo.walkaholic.databinding.ItemSlotShopBinding

class DashboardWeatherWebView : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard_weather_web_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentDashboardWeatherWebViewBinding.inflate(layoutInflater)

        val weatherWebView: WebView = binding.dashboardWeatherWebView
        // 웹뷰 안 새 창이 뜨지 않도록
        weatherWebView.webViewClient = WebViewClient()
        weatherWebView.webChromeClient = WebChromeClient()

        weatherWebView.settings.javaScriptEnabled = true
        weatherWebView.settings.useWideViewPort = true
        weatherWebView.settings.builtInZoomControls = true
        weatherWebView.settings.setSupportZoom(true)

        weatherWebView.loadUrl("https://weather.naver.com/today/09590109")
    }
}