package ru.sedavnyh.vkgroupscan.application

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import ru.sedavnyh.vkgroupscan.di.Scopes.APP_SCOPE
import ru.sedavnyh.vkgroupscan.di.apiModule
import ru.sedavnyh.vkgroupscan.di.databaseModule
import ru.sedavnyh.vkgroupscan.di.navigationModule
import toothpick.Toothpick
import javax.inject.Inject

class App @Inject constructor(): Application() {

    val channelId = "Progress Notification"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
        Toothpick
            .openScope(APP_SCOPE)
            .installModules(
                databaseModule(applicationContext),
                navigationModule(),
                apiModule())
    }


    private fun createNotificationChannels(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel1 = NotificationChannel(
                channelId,
                "Progress Notification",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel1.description = "Progress Notification Channel"
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(channel1)
        }
    }
}