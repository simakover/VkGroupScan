package ru.sedavnyh.vkgroupscan.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.sedavnyh.vkgroupscan.models.wallGetModel.Post
import ru.sedavnyh.vkgroupscan.util.Constants.DATABASE_VERSION

@Database(
    entities = [Post::class],
    version = DATABASE_VERSION,
    exportSchema = false
)
@TypeConverters(VkTypeConverter::class)
abstract class VkDatabase: RoomDatabase() {
    abstract fun vkDao(): VkDao
}