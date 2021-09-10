package ru.sedavnyh.vkgroupscan.models.groupsGetByIdModel


import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("is_closed")
    val isClosed: Int?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("photo_100")
    val photo100: String?,
    @SerializedName("photo_200")
    val photo200: String?,
    @SerializedName("photo_50")
    val photo50: String?,
    @SerializedName("screen_name")
    val screenName: String?,
    @SerializedName("type")
    val type: String?
)