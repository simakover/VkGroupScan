package ru.sedavnyh.vkgroupscan.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.sedavnyh.vkgroupscan.models.entities.Group
import ru.sedavnyh.vkgroupscan.models.wallGetCommentsModel.Comment
import ru.sedavnyh.vkgroupscan.models.wallGetModel.Post

@Dao
interface VkDao {
    //posts
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: Post)

    @Query("SELECT * FROM POSTS_TABLE WHERE ISPINNED is NULL ORDER BY DATE DESC")
    suspend fun selectPosts() : List<Post>

    @Query("SELECT COUNT(1) FROM POSTS_TABLE")
    suspend fun countPosts(): Int

    @Delete
    suspend fun deletePost(post: Post)

    //groups
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: Group)

    @Query("SELECT * FROM GROUPS_TABLE")
    suspend fun selectGroups(): List<Group>

    @Update
    suspend fun updateGroup(group: Group)

    //comments
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: Comment)

    @Query("DELETE FROM COMMENTS_TABLE")
    suspend fun deleteComments()

    @Query("SELECT COUNT(1) FROM COMMENTS_TABLE")
    suspend fun countComments(): Int
}