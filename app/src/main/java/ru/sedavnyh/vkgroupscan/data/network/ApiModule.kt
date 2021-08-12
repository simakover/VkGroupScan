package ru.sedavnyh.vkgroupscan.data.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.sedavnyh.vkgroupscan.util.Constants.BASE_URL
import ru.sedavnyh.vkgroupscan.util.Constants.TIME_OUT
import java.util.concurrent.TimeUnit

class ApiModule {
    fun getApiService(): Api {

        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(TIME_OUT, TimeUnit.SECONDS)
            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Api::class.java)
    }
}