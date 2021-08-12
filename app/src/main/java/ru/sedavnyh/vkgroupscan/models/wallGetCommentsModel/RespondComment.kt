package ru.sedavnyh.vkgroupscan.models.wallGetCommentsModel


import com.google.gson.annotations.SerializedName

data class RespondComment(
    @SerializedName("from_id")
    val fromId: Int?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("owner_id")
    val ownerId: Int?,
    @SerializedName("post_id")
    val postId: Int?,
    @SerializedName("text")
    val text: String?
)