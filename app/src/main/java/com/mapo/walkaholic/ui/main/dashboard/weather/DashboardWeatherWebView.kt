package com.mapo.walkaholic.ui.main.dashboard.weather

import android.net.http.SslError
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.fragment.app.Fragment
import com.mapo.walkaholic.R
import kotlinx.android.synthetic.main.fragment_dashboard_weather_web_view.view.*

class DashboardWeatherWebView : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //val binding = FragmentDashboardWeatherWebViewBinding.inflate(layoutInflater)
        val view = inflater.inflate(R.layout.fragment_dashboard_weather_web_view, container, false)

        // 웹뷰 안 새 창이 뜨지 않도록
        //weatherWebView.webChromeClient = WebChromeClient()
        //binding.dashboardWeatherWebView.webViewClient = WebViewClient()

        with(view.dashboardWeatherWebView.settings){
            javaScriptEnabled = true
            useWideViewPort = true
            builtInZoomControls = true
            setSupportZoom(true)
            domStorageEnabled = true

        }
        view.dashboardWeatherWebView.webViewClient = WeatherWebViewClient()

        val naverWeather = "https://weather.naver.com"
        view.dashboardWeatherWebView.loadUrl(naverWeather)

        return view
    }

    inner class WeatherWebViewClient : WebViewClient() {
        override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?
        ) {
            super.onReceivedSslError(view, handler, error)
            handler?.proceed()  // ssl 인증서 무시
        }

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            val target = request?.url.toString()
            if(target.contains("naver")){
                return false
            }
            return true
        }
    }
}