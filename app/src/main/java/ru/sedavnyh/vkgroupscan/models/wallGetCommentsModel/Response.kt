package ru.sedavnyh.vkgroupscan.models.wallGetCommentsModel


import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("items")
    val items: List<Comment>?
)