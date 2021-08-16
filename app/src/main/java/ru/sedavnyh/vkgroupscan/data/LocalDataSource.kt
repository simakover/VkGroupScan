package ru.sedavnyh.vkgroupscan.data

import ru.sedavnyh.vkgroupscan.data.database.VkDao
import ru.sedavnyh.vkgroupscan.models.entities.GroupEntity
import ru.sedavnyh.vkgroupscan.models.entities.PostEntity
import ru.sedavnyh.vkgroupscan.models.wallGetCommentsModel.Comment
import ru.sedavnyh.vkgroupscan.models.wallGetModel.Post
import javax.inject.Inject

class LocalDataSource @Inject constructor(private val vkDao: VkDao) {

    //posts
    suspend fun insertPost(post: PostEntity) {
        return vkDao.insertPost(post)
    }
    suspend fun selectPosts(): List<PostEntity> {
        return vkDao.selectPosts()
    }

    suspend fun countPosts(): Int {
        return vkDao.countPosts()
    }

    suspend fun deletePost(post: PostEntity) {
        return vkDao.deletePost(post)
    }

    //groups
    suspend fun insertGroup(groupEntity: GroupEntity) {
        return vkDao.insertGroup(groupEntity)
    }

    suspend fun selectGroups(): List<GroupEntity> {
        return vkDao.selectGroups()
    }

    suspend fun updateGroup(groupEntity: GroupEntity) {
        vkDao.updateGroup(groupEntity)
    }

    //comments
    suspend fun insertComment(comment: Comment) {
        vkDao.insertComment(comment)
    }

    suspend fun deleteComments() {
        vkDao.deleteComments()
    }

    suspend fun countComments(): Int {
        return vkDao.countComments()
    }
}