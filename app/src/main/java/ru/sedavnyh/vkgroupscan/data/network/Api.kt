package ru.sedavnyh.vkgroupscan.data.network

import retrofit2.http.GET
import retrofit2.http.Query
import ru.sedavnyh.vkgroupscan.models.groupsGetByIdModel.GroupsGetByIdResponse
import ru.sedavnyh.vkgroupscan.models.wallGetCommentsModel.WallGetCommentsResponse
import ru.sedavnyh.vkgroupscan.models.wallGetModel.WallGetResponse

interface Api {
    @GET("wall.get")
    suspend fun wallGet(
        @Query("access_token") accessToken: String,
        @Query("v") apiVersion: String,
        @Query("owner_id") ownerId: String,
        @Query("count") count: String,
        @Query("offset") offset: String
    ): WallGetResponse

    @GET("wall.getComments")
    suspend fun wallGetComments(
        @Query("access_token") accessToken: String,
        @Query("v") apiVersion: String,
        @Query("owner_id") ownerId: String,
        @Query("post_id") postId: String,
        @Query("thread_items_count") threadItemsCount: String
    ): WallGetCommentsResponse

    @GET("groups.getById")
    suspend fun groupsGetByID(
        @Query("access_token") accessToken: String,
        @Query("v") apiVersion: String,
        @Query("group_ids") group_ids: String
    ) : GroupsGetByIdResponse
}