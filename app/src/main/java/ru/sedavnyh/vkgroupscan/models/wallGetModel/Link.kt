package ru.sedavnyh.vkgroupscan.models.wallGetModel

import com.google.gson.annotations.SerializedName

data class Link (
    @SerializedName("title")
    val title: String?,
    @SerializedName("photo")
    val photo: Photo?
)