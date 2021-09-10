package ru.sedavnyh.vkgroupscan.viewModels

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.sedavnyh.vkgroupscan.R
import ru.sedavnyh.vkgroupscan.activities.MainActivity
import ru.sedavnyh.vkgroupscan.data.Repository
import ru.sedavnyh.vkgroupscan.di.Scopes.APP_SCOPE
import ru.sedavnyh.vkgroupscan.mappers.FromResponseToEntityMapper
import ru.sedavnyh.vkgroupscan.models.entities.GroupEntity
import ru.sedavnyh.vkgroupscan.models.entities.PostEntity
import ru.sedavnyh.vkgroupscan.util.Constants
import ru.sedavnyh.vkgroupscan.util.TextOperations
import toothpick.Toothpick
import javax.inject.Inject


class GroupViewModel : ViewModel() {

    @Inject
    lateinit var repository: Repository

    @Inject
    lateinit var context: Context

    @Inject
    lateinit var mapper: FromResponseToEntityMapper

    private lateinit var notification: NotificationCompat.Builder
    private var notificationManager: NotificationManagerCompat
    private val channelId = "Progress Notification"
    private var mSort: SharedPreferences

    init {
        Toothpick.inject(this, Toothpick.openScope(APP_SCOPE))
        notificationManager = NotificationManagerCompat.from(context)
        mSort = context.getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE)
    }

    fun getData(groupId: Int, priority: String = "DESC"): LiveData<List<PostEntity>> {
        return if (priority == "ASC")
            repository.local.selectPostsAscLiveData(groupId)
        else
            repository.local.selectPostsDescLiveData(groupId)
    }

    fun refreshComments(groupId: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            var posts = repository.local.selectPostsDesc(groupId)
            var loadedCommentsBefore = 0
            posts.forEach {
                loadedCommentsBefore += it.totalComments.size
            }

            var postCompleted = 0
            createNotification("Загрузка комментариев")

            posts.map { post ->
                var summaryComment: MutableList<String> = mutableListOf()
                summaryComment.add(post.text)
                summaryComment.addAll(post.totalComments)
                val loadedComments = repository.remote.wallGetComments(
                    post.ownerId.toString(),
                    post.id.toString()
                )
                Thread.sleep(500)
                try {
                    loadedComments.response?.items?.map {

                        it.text = TextOperations().cleanComment(it.text)
                        if (!it.text.isNullOrEmpty()) {
                            summaryComment.add(it.text!!)
                        }

                        it.respondThread?.items?.map { respComm ->
                            respComm.text = TextOperations().cleanComment(respComm.text)
                            if (!respComm.text.isNullOrEmpty()) {
                                summaryComment.add(respComm.text!!)
                            }
                        }
                    }

                    val cleanDescription = TextOperations().cleanDescription(post.text)
                    if (!cleanDescription.isNullOrEmpty()) {
                        cleanDescription.map {
                            summaryComment.add(it)
                        }
                    }

                    summaryComment.removeAll(listOf(""))

                    post.totalComments = summaryComment.toMutableSet().toMutableList()
                    repository.local.insertPost(post)
                } catch (e: Exception) {}

                postCompleted += 1
                updateNotification("Обработано постов: $postCompleted/${posts.size}", postCompleted, posts.size)
            }

            posts = repository.local.selectPostsDesc(groupId)
            var loadedCommentsAfter = 0
            posts.forEach {
                loadedCommentsAfter += it.totalComments.size
            }
            updateNotification("Загружено комментариев: ${loadedCommentsAfter - loadedCommentsBefore}")
        }
    }

    private fun createNotification(title: String) {
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

    private fun updateNotification(message: String, progress: Int = 0, maxProgress: Int = 0) {

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

    fun goToPostPage(post: PostEntity) {
        val uris = Uri.parse("https://vk.com/wall${post.ownerId}_${post.id}")
        val intents = Intent(Intent.ACTION_VIEW, uris)
        val bundle = Bundle()
        bundle.putBoolean("new_window", true)
        intents.putExtras(bundle)
        ContextCompat.startActivity(context, intents, bundle)
    }

    fun deletePost(post: PostEntity, position: Int) {
        GlobalScope.launch(Dispatchers.IO) { repository.local.deletePost(post) }
    }

    fun insertPost(post: PostEntity) {
        GlobalScope.launch(Dispatchers.IO) { repository.local.insertPost(post) }
    }

    fun openTachiyomiWithQuery(query: String) {
        val mainIntent = Intent().apply {
            action = "eu.kanade.tachiyomi.SEARCH"
            putExtra("query", query)
        }
        val bundle = Bundle()
        startActivity(context, mainIntent, bundle)
    }

    fun deleteGroup(group: GroupEntity) {
        GlobalScope.launch(Dispatchers.IO) { repository.local.deleteGroup(group) }
    }
}