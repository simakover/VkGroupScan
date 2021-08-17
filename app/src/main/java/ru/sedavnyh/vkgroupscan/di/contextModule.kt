package ru.sedavnyh.vkgroupscan.di

import android.content.Context
import android.widget.Spinner
import toothpick.ktp.binding.module

fun contextModule(context: Context, spinner: Spinner) = module {
    bind(Context::class.java).toInstance(context)
    bind(Spinner::class.java).toInstance(spinner)
}