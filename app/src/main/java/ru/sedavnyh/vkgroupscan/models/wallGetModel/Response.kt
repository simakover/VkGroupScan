package ru.sedavnyh.vkgroupscan.models.wallGetModel


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Response(
    @SerializedName("count")
    val count: Int?,
    @SerializedName("items")
    val posts: List<Post>?
): Serializable