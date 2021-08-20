package ru.sedavnyh.vkgroupscan.presenters

import android.app.PendingIntent
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import ru.sedavnyh.vkgroupscan.R
import ru.sedavnyh.vkgroupscan.activities.MainActivity
import ru.sedavnyh.vkgroupscan.data.Repository
import ru.sedavnyh.vkgroupscan.mappers.FromResponseToEntityMapper
import ru.sedavnyh.vkgroupscan.models.entities.GroupEntity
import ru.sedavnyh.vkgroupscan.models.entities.PostEntity
import ru.sedavnyh.vkgroupscan.models.wallGetCommentsModel.Comment
import ru.sedavnyh.vkgroupscan.models.wallGetCommentsModel.RespondThread
import ru.sedavnyh.vkgroupscan.navigation.Screens
import ru.sedavnyh.vkgroupscan.util.Constants.APP_PREFERENCES
import ru.sedavnyh.vkgroupscan.util.Constants.APP_PREFERENCE_SORT
import ru.sedavnyh.vkgroupscan.util.Constants.APP_PREFERENCE_SORT_GROUP
import ru.sedavnyh.vkgroupscan.util.Constants.POST_LOAD_COUNT
import ru.sedavnyh.vkgroupscan.util.TextOperations
import ru.sedavnyh.vkgroupscan.view.MainView
import javax.inject.Inject

class MainPresenter @Inject constructor(
    private val repository: Repository,
    private val mapper: FromResponseToEntityMapper,
    private var router: Router,
    private var context: Context
) : MvpPresenter<MainView>() {

    var lastItem: Int = 0
    lateinit var notification: NotificationCompat.Builder
    var notificationManager: NotificationManagerCompat
    val channelId = "Progress Notification"
    var mSort: SharedPreferences

    init {
        mSort = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        checkGroupsExists()
        setData()
        notificationManager = NotificationManagerCompat.from(context)
        setupVpRecyclers()
    }

    fun setupVpRecyclers() {
        GlobalScope.launch(Dispatchers.Main) {
            val groups : MutableList<GroupEntity> = mutableListOf()
            groups.add(
                GroupEntity(
                    0,
                    0,
                    "all",
                    ""
                )
            )
            repository.local.selectGroups().map {
                groups.add(it)
            }
            viewState.setDataToViewPager(groups)
        }
    }

    fun refreshComments() {
        GlobalScope.launch(Dispatchers.IO) {
            val groupId = mSort.getInt(APP_PREFERENCE_SORT_GROUP, 0)
            val loadedCommentsBefore = repository.local.countComments(groupId)
            repository.local.deleteComments(groupId)
            val posts = repository.local.selectPostsDesc(groupId)
            var postCompleted = 0
            createNotification("Загрузка комментариев")

            posts.map { post ->
                var summaryComment: MutableList<String> = mutableListOf()
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

                val cleanDescription = TextOperations().cleanDescription(post.text)
                if (!cleanDescription.isNullOrEmpty()) {
                    cleanDescription.map {
                        summaryComment.add(it)
                    }
                }

                summaryComment.removeAll(listOf(""))

                post.totalComments = summaryComment
                repository.local.insertPost(post)

                postCompleted += 1
                updateNotification("Обработано постов: $postCompleted/${posts.size}", postCompleted, posts.size)
            }
            val loadedCommentsAfter = repository.local.countComments(groupId)
            setData()
            updateNotification("Загружено комментариев: ${loadedCommentsAfter - loadedCommentsBefore}")
        }
    }

    fun insertIntoDb() {
        GlobalScope.launch(Dispatchers.IO) {
            val groups = repository.local.selectGroups()
            val loadedPostsBefore = repository.local.countPosts()
            var groupsCompleted = 0
            createNotification("Загрузка постов")
            groups.map { group ->
                var offset = 0
                var response = repository.remote.wallGet(group.id.toString(), "1", offset.toString())
                Thread.sleep(500)

                while (group.postCount < response?.count!!) {
                    var loadCount: Int
                    loadCount = if (response.posts?.first()?.isPinned == 1) {
                        response.count!! - group.postCount + 1
                    } else {
                        response.count!! - group.postCount
                    }

                    var notloaded = 0
                    if (loadCount > POST_LOAD_COUNT) {
                        notloaded = loadCount - POST_LOAD_COUNT
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

                    if (response?.posts?.first()?.isPinned == 1 && notloaded == 0) {
                        group.postCount += loadCount - 1
                    } else {
                        group.postCount += loadCount
                    }

                    offset += loadCount
                    repository.local.updateGroup(group)
                }
                groupsCompleted += 1
                updateNotification("Групп обработано: $groupsCompleted/${groups.size}", groupsCompleted, groups.size)
            }
            val loadedPostsAfter = repository.local.countPosts()
            setData()
            updateNotification("Загружено постов: ${loadedPostsAfter - loadedPostsBefore}")
        }
    }

    fun insertPost(post: PostEntity) {
        GlobalScope.launch(Dispatchers.IO) { repository.local.insertPost(post) }
        setData()
    }

    fun checkGroupsExists() {
        GlobalScope.launch(Dispatchers.IO) {
            val groups = repository.local.selectGroups()
            if (groups.isNullOrEmpty()) {
                var group = GroupEntity(
                    -140579116,
                    15500, // 15963
                    "скрины из кетайских пopномультеков 0.2",
                    "https://sun1-25.userapi.com/s/v1/ig2/UL3xepuF-U7gpdwOLU8CBePLBJDMAu9QmtFw_QiDrBZg-B1LdPvv_bBeevZM3p5mEj2Cl4cM4VzCu-UQ-rEqnu-8.jpg?size=50x50&quality=96&crop=175,0,449,449&ava=1"
                )
                repository.local.insertGroup(group)

                group = GroupEntity(
                    -192370022,
                    1500, // 2092
                    "a slice of doujin",
                    "https://sun1-13.userapi.com/s/v1/ig2/JtRDppZ2PqNu-rnWmxsqyvxDrOKqYTc3Jjkz_ChEV_c9grSMBZqL01TMacwfA7m5crENKZIZZUiUJBg0NqZkt5DH.jpg?size=50x50&quality=96&crop=104,4,908,908&ava=1"
                )
                repository.local.insertGroup(group)

                group = GroupEntity(
                    -184665352,
                    1000, // 1600
                    "doujin cap",
                    "https://sun1-29.userapi.com/s/v1/ig2/5EmyxrOTvObLCoEfwb3ZDpb6ena0pPrwkpm37ga1bPOs-JN1rff8KQL7EiFNY1rGPobxvVHMSavfz3mAg2rDCNYs.jpg?size=50x50&quality=96&crop=106,0,426,426&ava=1"
                )
                repository.local.insertGroup(group)
            }
        }
    }

    fun deletePost(post: PostEntity, position: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            repository.local.deletePost(post)
            lastItem = position
            setData()
        }
    }

    fun setData() {
        GlobalScope.launch(Dispatchers.Main) {
            val sortOrder = mSort.getString(APP_PREFERENCE_SORT, "DESC")
            val groupId = mSort.getInt(APP_PREFERENCE_SORT_GROUP, 0)
            val posts = if (sortOrder == "DESC") {
                repository.local.selectPostsDesc(groupId)
            } else {
                repository.local.selectPostsAsc(groupId)
            }
            viewState.setDataToRecycler(posts)
        }
    }

    fun goToPostPage(post: PostEntity) {
        val uris = Uri.parse("https://vk.com/wall${post.ownerId}_${post.id}")
        val intents = Intent(Intent.ACTION_VIEW, uris)
        val bundle = Bundle()
        bundle.putBoolean("new_window", true)
        intents.putExtras(bundle)
        ContextCompat.startActivity(context, intents, bundle)
    }

    fun navigateToImage(link: String, position: Int) {
        lastItem = position
        router.navigateTo(Screens.imageScreen(link))
    }

    fun copyCommentToClipboard(comment: String) {
        val myClipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val myClip: ClipData = ClipData.newPlainText("Label", comment)
        myClipboard.setPrimaryClip(myClip)
        Toast.makeText(context, "$comment copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    fun createNotification(title: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        notification =
            NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_update)
                .setContentTitle(title)
                .setContentText("Downloading")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setProgress(0, 0, true)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
        notificationManager.notify(1, notification.build())
    }

    fun updateNotification(message: String, progress: Int = 0, maxProgress: Int = 0) {

        if (progress != 0) {
            notification.setContentText(message)
                .setProgress(maxProgress, progress, false)

            notificationManager.notify(1, notification.build())
        } else {
            notification
                .setContentText(message)
                .setProgress(0, 0, false)
                .setOngoing(false)
            notificationManager.notify(1, notification.build())
        }
    }

    fun setSortOrder(sortOrder: String) {
        mSort.edit().putString(APP_PREFERENCE_SORT, sortOrder).apply()
    }
}