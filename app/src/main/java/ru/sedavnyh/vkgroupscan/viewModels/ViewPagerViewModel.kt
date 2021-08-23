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


class ViewPagerViewModel: ViewModel() {

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

    init{
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
                groupsCompleted += 1
                updateNotification("Групп обработано: $groupsCompleted/${groups.size}", groupsCompleted, groups.size)
            }
            val loadedPostsAfter = repository.local.countPosts()
            updateNotification("Загружено постов: ${loadedPostsAfter - loadedPostsBefore}")
        }
    }

    suspend fun getGroupFragments() : ArrayList<Fragment> {
        if (fragmentList.isNullOrEmpty()) {
            checkGroupsExists()
            val groups = repository.local.selectGroups()
            fragmentList.add(GroupFragment(GroupEntity(0, 0, "All", "")))
            groups.map {
                fragmentList.add(GroupFragment(it))
            }
        }
        return  fragmentList
    }

    private suspend fun checkGroupsExists() {
            val groups = repository.local.selectGroups()
            if (groups.isNullOrEmpty()) {
                var group = GroupEntity(
                    -140579116,
                    15963, // 15963
                    "скрины из кетайских пopномультеков 0.2",
                    "https://sun1-25.userapi.com/s/v1/ig2/UL3xepuF-U7gpdwOLU8CBePLBJDMAu9QmtFw_QiDrBZg-B1LdPvv_bBeevZM3p5mEj2Cl4cM4VzCu-UQ-rEqnu-8.jpg?size=50x50&quality=96&crop=175,0,449,449&ava=1"
                )
                repository.local.insertGroup(group)

                group = GroupEntity(
                    -192370022,
                    2092, // 2092
                    "a slice of doujin",
                    "https://sun1-13.userapi.com/s/v1/ig2/JtRDppZ2PqNu-rnWmxsqyvxDrOKqYTc3Jjkz_ChEV_c9grSMBZqL01TMacwfA7m5crENKZIZZUiUJBg0NqZkt5DH.jpg?size=50x50&quality=96&crop=104,4,908,908&ava=1"
                )
                repository.local.insertGroup(group)

                group = GroupEntity(
                    -184665352,
                    1600, // 1600
                    "doujin cap",
                    "https://sun1-29.userapi.com/s/v1/ig2/5EmyxrOTvObLCoEfwb3ZDpb6ena0pPrwkpm37ga1bPOs-JN1rff8KQL7EiFNY1rGPobxvVHMSavfz3mAg2rDCNYs.jpg?size=50x50&quality=96&crop=106,0,426,426&ava=1"
                )
                repository.local.insertGroup(group)
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
}