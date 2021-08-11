package ru.sedavnyh.vkgroupscan.models.wallGetModel


import com.google.gson.annotations.SerializedName

data class Attachment(
    @SerializedName("photo")
    val photo: Photo?,
    @SerializedName("type")
    val type: String?
)