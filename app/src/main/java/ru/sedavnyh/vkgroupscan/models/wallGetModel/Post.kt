package ru.sedavnyh.vkgroupscan.models.wallGetModel


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import ru.sedavnyh.vkgroupscan.util.Constants.POSTS_TABLE

@Entity(tableName = POSTS_TABLE)
data class Post(
    @PrimaryKey(autoGenerate = false)
    @SerializedName("id")
    val id: Int?/*,
    @SerializedName("attachments")
    val attachments: List<Attachment>?,
    @SerializedName("date")
    val date: Int?,
    @SerializedName("owner_id")
    val ownerId: Int?,
    @SerializedName("is_pinned")
    val isPinned: Int?,
    @SerializedName("marked_as_ads")
    val markedAsAds: Int?,
    @SerializedName("text")
    val text: String?*/
)