package ru.sedavnyh.vkgroupscan.models.groupsGetByIdModel


import com.google.gson.annotations.SerializedName

data class GroupsGetByIdResponse(
    @SerializedName("response")
    val response: List<Response>?
)