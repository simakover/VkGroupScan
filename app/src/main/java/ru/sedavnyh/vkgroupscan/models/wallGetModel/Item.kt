package ru.sedavnyh.vkgroupscan.models.wallGetModel


import com.google.gson.annotations.SerializedName

data class Item(
    @SerializedName("attachments")
    val attachments: List<Attachment>?,
    @SerializedName("date")
    val date: Int?,
    @SerializedName("owner_id")
    val ownerId: Int?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("is_pinned")
    val isPinned: Int?,
    @SerializedName("marked_as_ads")
    val markedAsAds: Int?,
    @SerializedName("text")
    val text: String?,
)