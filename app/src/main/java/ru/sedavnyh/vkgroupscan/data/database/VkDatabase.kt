package ru.sedavnyh.vkgroupscan.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.sedavnyh.vkgroupscan.models.wallGetModel.Item
import ru.sedavnyh.vkgroupscan.util.Constants.DATABASE_VERSION

@Database(
    entities = [Item::class],
    version = DATABASE_VERSION,
    exportSchema = false
)
abstract class VkDatabase: RoomDatabase() {
    abstract fun vkDao(): VkDao
}