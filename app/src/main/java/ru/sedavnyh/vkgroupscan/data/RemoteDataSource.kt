package ru.sedavnyh.vkgroupscan.data

import ru.sedavnyh.vkgroupscan.data.network.Api
import ru.sedavnyh.vkgroupscan.models.groupsGetByIdModel.GroupsGetByIdResponse
import ru.sedavnyh.vkgroupscan.models.wallGetCommentsModel.WallGetCommentsResponse
import ru.sedavnyh.vkgroupscan.models.wallGetModel.Response
import ru.sedavnyh.vkgroupscan.util.Constants.ACCESS_TOKEN
import ru.sedavnyh.vkgroupscan.util.Constants.API_VERSION
import ru.sedavnyh.vkgroupscan.util.Constants.THREAD_ITEMS_COUNT
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val vkApi: Api
) {
    suspend fun wallGet(
        ownerId: String,
        count: String,
        offset: String
    ): Response? {
        return vkApi.wallGet(ACCESS_TOKEN, API_VERSION, ownerId, count, offset).response
    }

    suspend fun wallGetComments(
        ownerId: String,
        postId: String
    ): WallGetCommentsResponse {
        return vkApi.wallGetComments(ACCESS_TOKEN, API_VERSION, ownerId, postId, THREAD_ITEMS_COUNT)
    }

    suspend fun groupsGetById(
        group_id: String
    ): GroupsGetByIdResponse {
        return vkApi.groupsGetByID(ACCESS_TOKEN, API_VERSION, group_id)
    }
}