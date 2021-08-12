package ru.sedavnyh.vkgroupscan.models.wallGetCommentsModel


import com.google.gson.annotations.SerializedName

data class WallGetCommentsResponse(
    @SerializedName("response")
    val response: Response?
)