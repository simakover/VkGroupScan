package ru.sedavnyh.vkgroupscan.di

import ru.sedavnyh.vkgroupscan.data.network.Api
import ru.sedavnyh.vkgroupscan.data.network.ApiModule
import toothpick.ktp.binding.module

fun apiModule() = module {

    val api = ApiModule().getApiService()

    bind(Api::class.java).toInstance(api)
}