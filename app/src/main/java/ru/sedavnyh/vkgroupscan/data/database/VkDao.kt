package ru.sedavnyh.vkgroupscan.data.database

import androidx.room.*
import ru.sedavnyh.vkgroupscan.models.entities.GroupEntity
import ru.sedavnyh.vkgroupscan.models.entities.PostEntity
import ru.sedavnyh.vkgroupscan.models.wallGetCommentsModel.Comment

@Dao
interface VkDao {
    //posts
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(postEntity: PostEntity)

    @Query("SELECT * FROM POSTS_TABLE WHERE ISPINNED = 0 ORDER BY DATE DESC")
    suspend fun selectPosts() : List<PostEntity>

    @Query("SELECT COUNT(1) FROM POSTS_TABLE")
    suspend fun countPosts(): Int

    @Delete
    suspend fun deletePost(post: PostEntity)

    //groups
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(groupEntity: GroupEntity)

    @Query("SELECT * FROM GROUPS_TABLE")
    suspend fun selectGroups(): List<GroupEntity>

    @Update
    suspend fun updateGroup(groupEntity: GroupEntity)

    //comments
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: Comment)

    @Query("DELETE FROM COMMENTS_TABLE")
    suspend fun deleteComments()

    @Query("SELECT COUNT(1) FROM COMMENTS_TABLE")
    suspend fun countComments(): Int
}