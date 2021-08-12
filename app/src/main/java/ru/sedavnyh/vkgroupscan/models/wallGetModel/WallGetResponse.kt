package ru.sedavnyh.vkgroupscan.models.wallGetModel


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class WallGetResponse(
    @SerializedName("response")
    val response: Response?
): Serializable