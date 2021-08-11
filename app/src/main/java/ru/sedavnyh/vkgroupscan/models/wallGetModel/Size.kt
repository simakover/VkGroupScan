package ru.sedavnyh.vkgroupscan.models.wallGetModel


import com.google.gson.annotations.SerializedName

data class Size(
    @SerializedName("height")
    val height: Int?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("url")
    val url: String?,
    @SerializedName("width")
    val width: Int?
)