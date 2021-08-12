package ru.sedavnyh.vkgroupscan.data.network

import retrofit2.http.GET
import retrofit2.http.Query
import ru.sedavnyh.vkgroupscan.models.wallGetModel.WallGetResponse

interface Api {
    @GET("wall.get")
    suspend fun wallGet(
        @Query("access_token") accessToken: String,
        @Query("v") apiVersion: String,
        @Query("owner_id") ownerId: String,
        @Query("count") count: String
    ): WallGetResponse
}