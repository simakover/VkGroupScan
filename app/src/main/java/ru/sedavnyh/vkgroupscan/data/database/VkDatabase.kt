package ru.sedavnyh.vkgroupscan.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.sedavnyh.vkgroupscan.models.entities.GroupEntity
import ru.sedavnyh.vkgroupscan.models.entities.PostEntity
import ru.sedavnyh.vkgroupscan.models.wallGetCommentsModel.Comment
import ru.sedavnyh.vkgroupscan.util.Constants.DATABASE_VERSION

@Database(
    entities = [GroupEntity::class, Comment::class, PostEntity::class],
    version = DATABASE_VERSION,
    exportSchema = false
)
@TypeConverters(VkTypeConverter::class)
abstract class VkDatabase: RoomDatabase() {
    abstract fun vkDao(): VkDao
}