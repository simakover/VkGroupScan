package ru.sedavnyh.vkgroupscan.data.network

import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query
import ru.sedavnyh.vkgroupscan.models.wallGetModel.WallGetResponse

interface Api {
    @GET("wall.get")
    fun wallGet(
        @Query("access_token") accessToken: String,
        @Query("v") apiVersion: String,
        @Query("owner_id") ownerId: String
    ): Single<WallGetResponse>
}