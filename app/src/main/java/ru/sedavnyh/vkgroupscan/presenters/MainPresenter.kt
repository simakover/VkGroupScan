package ru.sedavnyh.vkgroupscan.presenters

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import ru.sedavnyh.vkgroupscan.data.Repository
import ru.sedavnyh.vkgroupscan.mappers.FromResponseToEntityMapper
import ru.sedavnyh.vkgroupscan.models.entities.GroupEntity
import ru.sedavnyh.vkgroupscan.models.entities.PostEntity
import ru.sedavnyh.vkgroupscan.models.wallGetCommentsModel.Comment
import ru.sedavnyh.vkgroupscan.models.wallGetCommentsModel.RespondThread
import ru.sedavnyh.vkgroupscan.navigation.Screens
import ru.sedavnyh.vkgroupscan.util.Constants.POST_LOAD_COUNT
import ru.sedavnyh.vkgroupscan.util.TextOperations
import ru.sedavnyh.vkgroupscan.view.MainView
import javax.inject.Inject

class MainPresenter @Inject constructor(
    private val repository: Repository,
    private val mapper: FromResponseToEntityMapper,
    private var router : Router
) : MvpPresenter<MainView>() {

    var lastItem : Int = 0
    lateinit var notification: NotificationCompat.Builder

    init{
        checkGroupsExists()
        setData()
    }

    fun refreshComments() {
        GlobalScope.launch(Dispatchers.Main) {
            val loadedCommentsBefore = repository.local.countComments()
            repository.local.deleteComments()
            val posts = repository.local.selectPosts()
            var postCompleted = 0
            viewState.createNotification("Загрузка комментариев")

            posts.map { post ->
                var summaryComment : MutableList<String> = mutableListOf()
                val loadedComments = repository.remote.wallGetComments(
                    post.ownerId.toString(),
                    post.id.toString()
                )
                Thread.sleep(500)
                loadedComments.response?.items?.map {

                    it.text = TextOperations().cleanComment(it.text)
                    if (!it.text.isNullOrEmpty()) {
                        summaryComment.add(it.text!!)
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
                        if (!respComm.text.isNullOrEmpty()) {
                            summaryComment.add(respComm.text!!)
                        }
                        repository.local.insertComment(comment)
                    }
                }
                post.totalComments = summaryComment
                repository.local.insertPost(post)

                postCompleted += 1
                viewState.updateNotification("Обработано постов: $postCompleted/${posts.size}", postCompleted, posts.size)
            }
            val loadedCommentsAfter = repository.local.countComments()
            setData()
            viewState.updateNotification("Загружено комментариев: ${loadedCommentsAfter - loadedCommentsBefore}")
            viewState.sendToast("Загружено комментариев: ${loadedCommentsAfter - loadedCommentsBefore}")
        }
    }

    fun insertIntoDb() {
        GlobalScope.launch(Dispatchers.Main) {
            val groups = repository.local.selectGroups()
            val loadedPostsBefore = repository.local.countPosts()
            var groupsCompleted = 0
            viewState.createNotification("Загрузка постов")
            groups.map { group ->
                var offset = 0
                var response = repository.remote.wallGet(group.id.toString(), "1", offset.toString())
                Thread.sleep(500)

                while (group.postCount < response?.count!!) {
                    var loadCount : Int
                    loadCount = if (response.posts?.first()?.isPinned == 1) {
                        response.count!! - group.postCount + 1
                    } else {
                        response.count!! - group.postCount
                    }

                    if (loadCount > POST_LOAD_COUNT) {
                        loadCount = POST_LOAD_COUNT
                    }

                    response =
                        repository.remote.wallGet(group.id.toString(), loadCount.toString(), offset.toString())
                    Thread.sleep(500)

                    response?.posts?.map { post ->
                        post.groupAvatar = group.avatar
                        post.groupName = group.title
                        repository.local.insertPost(mapper.mapToPostEntity(post))
                    }

                    group.postCount += loadCount
                    offset += loadCount
                    repository.local.updateGroup(group)
                }
                groupsCompleted += 1
                viewState.updateNotification("Групп обработано: $groupsCompleted/${groups.size}", groupsCompleted, groups.size)
            }
            val loadedPostsAfter = repository.local.countPosts()
            setData()
            viewState.updateNotification("Загружено постов: ${loadedPostsAfter - loadedPostsBefore}")
            viewState.sendToast("Загружено постов: ${loadedPostsAfter - loadedPostsBefore}")
        }
    }

    fun checkGroupsExists() {
        GlobalScope.launch(Dispatchers.Main) {
            val groups = repository.local.selectGroups()
            if (groups.isNullOrEmpty()) {
                var group = GroupEntity(
                    -140579116,
                    15400, // 15963  = 563
                    "скрины из кетайских пopномультеков 0.2",
                    "https://sun1-25.userapi.com/s/v1/ig2/UL3xepuF-U7gpdwOLU8CBePLBJDMAu9QmtFw_QiDrBZg-B1LdPvv_bBeevZM3p5mEj2Cl4cM4VzCu-UQ-rEqnu-8.jpg?size=50x50&quality=96&crop=175,0,449,449&ava=1"
                )
                repository.local.insertGroup(group)

                group = GroupEntity(
                    -192370022,
                    1500, // 2092 = 592
                    "a slice of doujin",
                    "https://sun1-13.userapi.com/s/v1/ig2/JtRDppZ2PqNu-rnWmxsqyvxDrOKqYTc3Jjkz_ChEV_c9grSMBZqL01TMacwfA7m5crENKZIZZUiUJBg0NqZkt5DH.jpg?size=50x50&quality=96&crop=104,4,908,908&ava=1"
                )
                repository.local.insertGroup(group)

                group = GroupEntity(
                    -184665352,
                    1000, // 1600 = 600
                    "doujin cap",
                    "https://sun1-29.userapi.com/s/v1/ig2/5EmyxrOTvObLCoEfwb3ZDpb6ena0pPrwkpm37ga1bPOs-JN1rff8KQL7EiFNY1rGPobxvVHMSavfz3mAg2rDCNYs.jpg?size=50x50&quality=96&crop=106,0,426,426&ava=1"
                )
                repository.local.insertGroup(group)
            }
        }
    }

    fun deletePost(post: PostEntity, position: Int) {
        GlobalScope.launch(Dispatchers.Main) {
            repository.local.deletePost(post)
            lastItem = position
            setData()
        }
    }

    fun setData() {
        GlobalScope.launch(Dispatchers.Main) {
            val posts = repository.local.selectPosts()
            viewState.setDataToRecycler(posts)
        }
    }

    fun goToPostPage(post: PostEntity) {
        val uris = Uri.parse("https://vk.com/wall${post.ownerId}_${post.id}")
        val intents = Intent(Intent.ACTION_VIEW, uris)
        val bundle = Bundle()
        bundle.putBoolean("new_window", true)
        intents.putExtras(bundle)
        viewState.goToPostPage(intents, bundle)
    }

    fun navigateToImage(link : String, position: Int) {
        lastItem = position
        router.navigateTo(Screens.imageScreen(link))
    }

    fun copyCommentToClipboard(comment: String) {
        viewState.copyCommentToClipboard(comment)
    }

    fun exportPosts() {
        TODO("Not yet implemented")
    }

    fun importPosts() {
        TODO("Not yet implemented")
    }
}