package ru.sedavnyh.vkgroupscan.application

import android.app.Application
import ru.sedavnyh.vkgroupscan.di.Scopes.APP_SCOPE
import ru.sedavnyh.vkgroupscan.di.apiModule
import ru.sedavnyh.vkgroupscan.di.databaseModule
import ru.sedavnyh.vkgroupscan.di.navigationModule
import toothpick.Toothpick
import javax.inject.Inject

class App @Inject constructor(): Application() {
    override fun onCreate() {
        super.onCreate()
        Toothpick
            .openScope(APP_SCOPE)
            .installModules(
                databaseModule(applicationContext),
                navigationModule(),
                apiModule())
    }
}