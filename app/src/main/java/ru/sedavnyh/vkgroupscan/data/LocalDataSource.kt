package ru.sedavnyh.vkgroupscan.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.sedavnyh.vkgroupscan.data.database.VkDao
import ru.sedavnyh.vkgroupscan.models.entities.GroupEntity
import ru.sedavnyh.vkgroupscan.models.entities.PostEntity
import ru.sedavnyh.vkgroupscan.models.wallGetCommentsModel.Comment
import javax.inject.Inject

class LocalDataSource @Inject constructor(private val vkDao: VkDao) {

    //posts
    suspend fun insertPost(post: PostEntity) {
        return vkDao.insertPost(post)
    }
    suspend fun selectPostsDesc(groupId : Int = 0): List<PostEntity> {
        return vkDao.selectPostsDesc(groupId)
    }

    suspend fun countPosts(): Int {
        return vkDao.countPosts()
    }

    suspend fun deletePost(post: PostEntity) {
        return vkDao.deletePost(post)
    }
    // posts LiveData
    fun selectPostsAscLiveData(groupId: Int = 0): LiveData<List<PostEntity>> {
        return vkDao.selectPostsAscLiveData(groupId)
    }

    fun selectPostsDescLiveData(groupId: Int = 0): LiveData<List<PostEntity>> {
        return vkDao.selectPostsDescLiveData(groupId)
    }

    fun postCount(groupId: Int = 0): LiveData<Int> {
        return vkDao.postCount(groupId)
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

    suspend fun deleteComments(groupId: Int = 0) {
        vkDao.deleteComments(groupId)
    }

    suspend fun countComments(groupId: Int = 0): Int {
        return vkDao.countComments(groupId)
    }
}