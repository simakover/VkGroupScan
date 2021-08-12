package ru.sedavnyh.vkgroupscan.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import io.reactivex.rxjava3.core.Single
import ru.sedavnyh.vkgroupscan.models.groupsModel.Group
import ru.sedavnyh.vkgroupscan.models.wallGetModel.Post

@Dao
interface VkDao {
    //posts
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: Post)

    @Query("SELECT * FROM POSTS_TABLE")
    fun selectPosts() : LiveData<List<Post>>

    //groups
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: Group)

    @Query("SELECT * FROM GROUPS_TABLE")
    suspend fun selectGroups(): List<Group>

    @Update
    suspend fun updateGroup(group: Group)
}