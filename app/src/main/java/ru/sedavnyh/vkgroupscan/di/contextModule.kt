package ru.sedavnyh.vkgroupscan.di

import android.content.Context
import toothpick.ktp.binding.module

fun contextModule(context: Context) = module {
    bind(Context::class.java).toInstance(context)
}