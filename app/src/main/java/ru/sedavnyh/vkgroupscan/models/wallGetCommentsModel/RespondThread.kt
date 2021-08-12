package ru.sedavnyh.vkgroupscan.models.wallGetCommentsModel


import com.google.gson.annotations.SerializedName

data class RespondThread(
    @SerializedName("items")
    val items: List<RespondComment>?
)