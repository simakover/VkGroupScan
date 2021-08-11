package ru.sedavnyh.vkgroupscan.models.wallGetModel


import com.google.gson.annotations.SerializedName

data class Photo(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("post_id")
    val postId: Int?,
    @SerializedName("sizes")
    val sizes: List<Size>?
)