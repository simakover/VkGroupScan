package ru.sedavnyh.vkgroupscan.models.wallGetModel

import com.google.gson.annotations.SerializedName

data class Album (
    @SerializedName("id")
    val id: Int?,
    @SerializedName("owner_id")
    val ownerId: Int?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("thumb")
    val thumb: Thumb?
)