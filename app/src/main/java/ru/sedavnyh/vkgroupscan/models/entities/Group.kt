package ru.sedavnyh.vkgroupscan.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.sedavnyh.vkgroupscan.util.Constants.GROUPS_TABLE

@Entity(tableName = GROUPS_TABLE)
data class Group (
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    var postCount : Int,
    var title: String,
    var avatar: String
)