package ru.sedavnyh.vkgroupscan.models.wallGetModel


import com.google.gson.annotations.SerializedName

data class WallGetResponse(
    @SerializedName("response")
    val response: Response?
)