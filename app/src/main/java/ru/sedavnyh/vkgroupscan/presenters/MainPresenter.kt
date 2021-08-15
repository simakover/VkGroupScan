package ru.sedavnyh.vkgroupscan.presenters

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import ru.sedavnyh.vkgroupscan.data.Repository
import ru.sedavnyh.vkgroupscan.models.entities.Group
import ru.sedavnyh.vkgroupscan.models.wallGetCommentsModel.Comment
import ru.sedavnyh.vkgroupscan.models.wallGetCommentsModel.RespondThread
import ru.sedavnyh.vkgroupscan.models.wallGetModel.Post
import ru.sedavnyh.vkgroupscan.util.Constants.POST_LOAD_COUNT
import ru.sedavnyh.vkgroupscan.util.TextOperations
import ru.sedavnyh.vkgroupscan.view.MainView
import javax.inject.Inject

class MainPresenter @Inject constructor(
    private val repository: Repository
) : MvpPresenter<MainView>() {
    fun refreshComments() {
        Log.d("inserting comment", "start")
        GlobalScope.launch(Dispatchers.Main) {
            val loadedCommentsBefore = repository.local.countComments()
            repository.local.deleteComments()
            val posts = repository.local.selectPostsForComments()

            posts.map { post ->
                var summaryComment = ""
                val loadedComments = repository.remote.wallGetComments(
                    post.ownerId.toString(),
                    post.id.toString()
                )
                Thread.sleep(500)
                loadedComments.response?.items?.map {

                    it.text = TextOperations().cleanComment(it.text)
                    if (it.text != null) {
                        Log.d("Add to summary", "${it.text}")
                        summaryComment = summaryComment.trim() + System.lineSeparator() + it.text
                    }
                    repository.local.insertComment(it)
                    it.respondThread?.items?.map { respComm ->
                        val comment = Comment(
                            respComm.id,
                            respComm.fromId,
                            respComm.ownerId,
                            respComm.postId,
                            respComm.text,
                            RespondThread(null)
                        )
                        respComm.text = TextOperations().cleanComment(respComm.text)
                        if (respComm.text != null) {
                            summaryComment = summaryComment.trim() + System.lineSeparator() + respComm.text
                        }
                        repository.local.insertComment(comment)
                    }
                }
                post.totalComments = summaryComment.trim()
                repository.local.insertPost(post)
            }
            val loadedCommentsAfter = repository.local.countComments()
            setData()
            viewState.sendToast("Loaded comments: ${loadedCommentsAfter - loadedCommentsBefore}")
        }
    }

    fun insertIntoDb() {
        GlobalScope.launch(Dispatchers.Main) {
            val groups = repository.local.selectGroups()
            val loadedPostsBefore = repository.local.countPosts()
            groups.map { group ->
                var response = repository.remote.wallGet(group.id.toString(), "1").response
                Thread.sleep(500)

                if (group.postCount < response?.count!!) {
                    var loadCount : Int
                    loadCount = if (response.posts?.first()?.isPinned == 1) {
                        response.count!! - group.postCount + 1
                    } else {
                        response.count!! - group.postCount
                    }

                    if (loadCount > POST_LOAD_COUNT)
                        loadCount = POST_LOAD_COUNT

                    group.postCount = response.count!!

                    response =
                        repository.remote.wallGet(group.id.toString(), loadCount.toString()).response
                    Thread.sleep(500)

                    response?.posts?.map { post ->
                        post.groupAvatar = group.avatar
                        post.groupName = group.title
                        repository.local.insertPost(post)
                    }
                    repository.local.updateGroup(group)
                }
            }
            val loadedPostsAfter = repository.local.countPosts()
            setData()
            viewState.sendToast("Loaded posts: ${loadedPostsAfter - loadedPostsBefore}")
        }
    }

    fun checkGroupsExists() {
        GlobalScope.launch(Dispatchers.Main) {
            val groups = repository.local.selectGroups()
            if (groups.isNullOrEmpty()) {
                var group = Group(
                    -140579116,
                    0,
                    "скрины из кетайских пopномультеков 0.2",
                    "https://sun1-25.userapi.com/s/v1/ig2/UL3xepuF-U7gpdwOLU8CBePLBJDMAu9QmtFw_QiDrBZg-B1LdPvv_bBeevZM3p5mEj2Cl4cM4VzCu-UQ-rEqnu-8.jpg?size=50x50&quality=96&crop=175,0,449,449&ava=1"
                )
                repository.local.insertGroup(group)

                group = Group(
                    -192370022,
                    0,
                    "a slice of doujin",
                    "https://sun1-13.userapi.com/s/v1/ig2/JtRDppZ2PqNu-rnWmxsqyvxDrOKqYTc3Jjkz_ChEV_c9grSMBZqL01TMacwfA7m5crENKZIZZUiUJBg0NqZkt5DH.jpg?size=50x50&quality=96&crop=104,4,908,908&ava=1"
                )
                repository.local.insertGroup(group)

                group = Group(
                    -184665352,
                    0,
                    "doujin cap",
                    "https://sun1-29.userapi.com/s/v1/ig2/5EmyxrOTvObLCoEfwb3ZDpb6ena0pPrwkpm37ga1bPOs-JN1rff8KQL7EiFNY1rGPobxvVHMSavfz3mAg2rDCNYs.jpg?size=50x50&quality=96&crop=106,0,426,426&ava=1"
                )
                repository.local.insertGroup(group)
            }
        }
    }

    fun deletePost(post: Post) {
        GlobalScope.launch(Dispatchers.Main) {
            repository.local.deletePost(post)
            setData()
        }
    }

    fun setData() {
        GlobalScope.launch(Dispatchers.Main) {
            val posts = repository.local.selectPostsForComments()
            viewState.setDataToRecycler(posts)
        }
    }

    fun exportPosts() {
        TODO("Not yet implemented")
    }

    fun importPosts() {
        TODO("Not yet implemented")
    }
}