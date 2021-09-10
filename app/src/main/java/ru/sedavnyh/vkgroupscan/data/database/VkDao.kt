package ru.sedavnyh.vkgroupscan.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.sedavnyh.vkgroupscan.models.entities.GroupEntity
import ru.sedavnyh.vkgroupscan.models.entities.PostEntity

@Dao
interface VkDao {
    //posts
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(postEntity: PostEntity)

    @Query("SELECT * FROM POSTS_TABLE WHERE ISPINNED = 0 AND ((:groupId = 0 and ownerId = ownerId) or (ownerId = :groupId)) ORDER BY DATE DESC")
    suspend fun selectPostsDesc(groupId: Int) : List<PostEntity>

    @Query("SELECT COUNT(1) FROM POSTS_TABLE")
    suspend fun countPosts(): Int

    @Delete
    suspend fun deletePost(post: PostEntity)

    //posts LiveData
    @Query("SELECT * FROM POSTS_TABLE WHERE ISPINNED = 0 and ((:groupId = 0 and ownerId = ownerId) or (ownerId = :groupId)) ORDER BY DATE ASC")
    fun selectPostsAscLiveData(groupId: Int) : LiveData<List<PostEntity>>

    @Query("SELECT * FROM POSTS_TABLE WHERE ISPINNED = 0 and ((:groupId = 0 and ownerId = ownerId) or (ownerId = :groupId)) ORDER BY DATE DESC")
    fun selectPostsDescLiveData(groupId: Int) : LiveData<List<PostEntity>>

    @Query("SELECT COUNT(1) FROM POSTS_TABLE WHERE ISPINNED = 0 and ((:groupId = 0 and ownerId = ownerId) or (ownerId = :groupId))")
    fun postCount(groupId: Int) : LiveData<Int>


    //groups
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(groupEntity: GroupEntity)

    @Query("SELECT * FROM GROUPS_TABLE")
    suspend fun selectGroups(): List<GroupEntity>

    @Update
    suspend fun updateGroup(groupEntity: GroupEntity)

    @Delete
    suspend fun deleteGroup(group: GroupEntity)
}