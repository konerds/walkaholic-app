package com.mapo.walkaholic.data.network

import com.mapo.walkaholic.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RemoteDataSource {
    companion object {
        private const val BACKEND_BASE_URL = BuildConfig.BACKEND_BASE_URL
        private const val BASE_URL_OPENAPI_APIS = BuildConfig.BASE_URL_OPENAPI_APIS
        private const val BASE_URL_OPENAPI_SGIS = BuildConfig.BASE_URL_OPENAPI_SGIS
    }

    fun <Api> buildRetrofitGuestApi(
        api: Class<Api>
    ): Api {
        return Retrofit.Builder()
            .baseUrl(BACKEND_BASE_URL)
            .client(
                OkHttpClient.Builder().also { client ->
                    if (BuildConfig.DEBUG) {
                        val logging = HttpLoggingInterceptor()
                        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                        client.addInterceptor(logging)
                    }
                }.build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(api)
    }

    fun <Api> buildRetrofitInnerApi(
        api: Class<Api>,
        jwtToken: String? = null,
        isSignup: Boolean
    ): Api {
        return Retrofit.Builder()
            .baseUrl(BACKEND_BASE_URL)
            .client(OkHttpClient.Builder().addInterceptor { chain ->
                chain.proceed(chain.request().newBuilder().also {
                    it.addHeader(
                        if (isSignup) {
                            "token"
                        } else {
                            "auth"
                        }, "$jwtToken"
                    )
                }.build())
            }
                .also { client ->
                    if (BuildConfig.DEBUG) {
                        val logging = HttpLoggingInterceptor()
                        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                        client.addInterceptor(logging)
                    }
                }.build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(api)
    }

    fun <Api> buildRetrofitApiWeatherAPI(
        api: Class<Api>
    ): Api {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_OPENAPI_APIS)
            .client(
                OkHttpClient.Builder().also { client ->
                    if (BuildConfig.DEBUG) {
                        val logging = HttpLoggingInterceptor()
                        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                        client.addInterceptor(logging)
                    }
                }.build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(api)
    }

    fun <Api> buildRetrofitApiSGISAPI(
        api: Class<Api>
    ): Api {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_OPENAPI_SGIS)
            .client(
                OkHttpClient.Builder().also { client ->
                    if (BuildConfig.DEBUG) {
                        val logging = HttpLoggingInterceptor()
                        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                        client.addInterceptor(logging)
                    }
                }.build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(api)
    }
}