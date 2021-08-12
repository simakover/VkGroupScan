package ru.sedavnyh.vkgroupscan.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.rxjava3.core.Single
import ru.sedavnyh.vkgroupscan.models.wallGetModel.Post

@Dao
interface VkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: Post)

    @Query("SELECT * FROM POSTS_TABLE")
    suspend fun selectPosts() : List<Post>
}