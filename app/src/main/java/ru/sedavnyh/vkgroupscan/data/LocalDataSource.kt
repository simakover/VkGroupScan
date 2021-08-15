package ru.sedavnyh.vkgroupscan.data

import ru.sedavnyh.vkgroupscan.data.database.VkDao
import ru.sedavnyh.vkgroupscan.models.entities.Group
import ru.sedavnyh.vkgroupscan.models.wallGetCommentsModel.Comment
import ru.sedavnyh.vkgroupscan.models.wallGetModel.Post
import javax.inject.Inject

class LocalDataSource @Inject constructor(private val vkDao: VkDao) {

    //posts
    suspend fun insertPost(post: Post) {
        return vkDao.insertPost(post)
    }

    suspend fun selectPostsForComments(): List<Post> {
        return vkDao.selectPosts()
    }

    suspend fun countPosts(): Int {
        return vkDao.countPosts()
    }

    suspend fun deletePost(post: Post) {
        return vkDao.deletePost(post)
    }

    //groups
    suspend fun insertGroup(group: Group) {
        return vkDao.insertGroup(group)
    }

    suspend fun selectGroups(): List<Group> {
        return vkDao.selectGroups()
    }

    suspend fun updateGroup(group: Group) {
        vkDao.updateGroup(group)
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