package ru.sedavnyh.vkgroupscan.models.wallGetCommentsModel


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import ru.sedavnyh.vkgroupscan.util.Constants.COMMENTS_TABLE

@Entity(tableName = COMMENTS_TABLE)
data class Comment(
    @PrimaryKey(autoGenerate = false)
    @SerializedName("id")
    val id: Int?,
    @SerializedName("from_id")
    val fromId: Int?,
    @SerializedName("owner_id")
    val ownerId: Int?,
    @SerializedName("post_id")
    val postId: Int?,
    @SerializedName("text")
    val text: String?,
    @SerializedName("thread")
    val respondThread: RespondThread?
)