package ru.sedavnyh.vkgroupscan.models.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import ru.sedavnyh.vkgroupscan.util.Constants

@Entity(tableName = Constants.POSTS_TABLE)
@Parcelize
data class PostEntity (
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val date: Int,
    val ownerId: Int,
    val isPinned: Int = 0,
    val markedAsAds: Int,
    val text: String,
    var groupAvatar: String,
    var groupName: String,
    var totalComments: MutableList<String>,
    val images: List<String>
) : Parcelable