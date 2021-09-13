package ru.sedavnyh.vkgroupscan.viewModels

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.sedavnyh.vkgroupscan.R
import ru.sedavnyh.vkgroupscan.activities.MainActivity
import ru.sedavnyh.vkgroupscan.data.Repository
import ru.sedavnyh.vkgroupscan.di.Scopes
import ru.sedavnyh.vkgroupscan.fragments.GroupFragment
import ru.sedavnyh.vkgroupscan.mappers.FromResponseToEntityMapper
import ru.sedavnyh.vkgroupscan.models.entities.GroupEntity
import ru.sedavnyh.vkgroupscan.util.Constants
import toothpick.Toothpick
import javax.inject.Inject


class ViewPagerViewModel : ViewModel() {

    @Inject
    lateinit var repository: Repository

    @Inject
    lateinit var context: Context

    @Inject
    lateinit var mapper: FromResponseToEntityMapper

    private lateinit var notification: NotificationCompat.Builder
    private var notificationManager: NotificationManagerCompat
    private val channelId = "Progress Notification"

    private val fragmentList: ArrayList<Fragment> = mutableListOf<Fragment>() as ArrayList<Fragment>

    init {
        Toothpick.inject(this, Toothpick.openScope(Scopes.APP_SCOPE))
        notificationManager = NotificationManagerCompat.from(context)
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
                try {
                    while (group.postCount < response?.count!!) {
                        var loadCount: Int
                        loadCount = if (response.posts?.first()?.isPinned == 1) {
                            response.count!! - group.postCount + 1
                        } else {
                            response.count!! - group.postCount
                        }

                        var notloaded = 0
                        if (loadCount > Constants.POST_LOAD_COUNT) {
                            notloaded = loadCount - Constants.POST_LOAD_COUNT
                            loadCount = Constants.POST_LOAD_COUNT
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
                } catch (e: Exception) { }
                groupsCompleted += 1
                updateNotification("Групп обработано: $groupsCompleted/${groups.size}", groupsCompleted, groups.size)
            }
            val loadedPostsAfter = repository.local.countPosts()
            updateNotification("Загружено постов: ${loadedPostsAfter - loadedPostsBefore}")
        }
    }

    suspend fun getGroupFragments(): ArrayList<Fragment> {
        if (fragmentList.isNullOrEmpty()) {
            val groups = repository.local.selectGroups()
            fragmentList.add(GroupFragment().constructFragment(GroupEntity(0, 0, "All", "")))
            groups.map {
                fragmentList.add(GroupFragment().constructFragment(it))
            }
        }
        return fragmentList
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
}