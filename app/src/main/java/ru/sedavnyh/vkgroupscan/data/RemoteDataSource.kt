package ru.sedavnyh.vkgroupscan.data

import ru.sedavnyh.vkgroupscan.data.network.Api
import ru.sedavnyh.vkgroupscan.models.wallGetCommentsModel.WallGetCommentsResponse
import ru.sedavnyh.vkgroupscan.models.wallGetModel.WallGetResponse
import ru.sedavnyh.vkgroupscan.util.Constants.ACCESS_TOKEN
import ru.sedavnyh.vkgroupscan.util.Constants.API_VERSION
import ru.sedavnyh.vkgroupscan.util.Constants.THREAD_ITEMS_COUNT
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val vkApi: Api) {
    suspend fun wallGet(
        ownerId: String,
        count: String
    ): WallGetResponse {
        return vkApi.wallGet(ACCESS_TOKEN, API_VERSION, ownerId, count)
    }

    suspend fun wallGetComments(
        ownerId: String,
        postId: String
    ): WallGetCommentsResponse {
        return vkApi.wallGetComments(ACCESS_TOKEN, API_VERSION, ownerId, postId, THREAD_ITEMS_COUNT)
    }
}