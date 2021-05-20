package com.mapo.walkaholic.data.network

import androidx.databinding.library.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RemoteDataSource {
    companion object {
        private const val BASE_URL = "http://15.164.103.223:8080/api/v1/"
        private const val BASE_URL_OPENAPI_APIS = "http://apis.data.go.kr/"
        private const val BASE_URL_OPENAPI_SGIS = "https://sgisapi.kostat.go.kr/OpenAPI3/"
    }

    fun <Api> buildRetrofitGuestApi(
        api: Class<Api>
    ): Api {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(OkHttpClient.Builder().also { client ->
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
        jwtToken: String ?= null
    ): Api {
        return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(OkHttpClient.Builder().addInterceptor { chain ->
                    chain.proceed(chain.request().newBuilder().also {
                        it.addHeader("Authorization", "Bearer $jwtToken")
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
                .client(OkHttpClient.Builder().also { client ->
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
                .client(OkHttpClient.Builder().also { client ->
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