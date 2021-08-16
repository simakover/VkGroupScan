package ru.sedavnyh.vkgroupscan.di


import android.content.Context
import androidx.room.Room
import ru.sedavnyh.vkgroupscan.data.database.VkDao
import ru.sedavnyh.vkgroupscan.data.database.VkDatabase
import ru.sedavnyh.vkgroupscan.util.Constants
import toothpick.ktp.binding.module

fun databaseModule(context: Context) = module {

    val db = Room.databaseBuilder(
        context,
        VkDatabase::class.java,
        Constants.DATABASE_NAME
    ).build()

    val dao = db.vkDao()

    bind(VkDao::class.java).toInstance(dao)
    bind(Context::class.java).toInstance(context)
}