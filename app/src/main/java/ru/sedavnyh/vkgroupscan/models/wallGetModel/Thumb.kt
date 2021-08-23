package ru.sedavnyh.vkgroupscan.models.wallGetModel

import com.google.gson.annotations.SerializedName

data class Thumb(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("owner_id")
    val ownerId: Int?,
    @SerializedName("album_id")
    val albumId: Int?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("sizes")
    val sizes: List<Size>?
)