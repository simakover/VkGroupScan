package ru.sedavnyh.vkgroupscan.fragments

import android.app.PendingIntent
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.os.SystemClock
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.sedavnyh.vkgroupscan.R
import ru.sedavnyh.vkgroupscan.activities.MainActivity
import ru.sedavnyh.vkgroupscan.adapters.PostAdapter
import ru.sedavnyh.vkgroupscan.databinding.FragmentPostsBinding
import ru.sedavnyh.vkgroupscan.di.Scopes.APP_SCOPE
import ru.sedavnyh.vkgroupscan.models.entities.PostEntity
import ru.sedavnyh.vkgroupscan.presenters.MainPresenter
import ru.sedavnyh.vkgroupscan.view.MainView
import toothpick.Toothpick
import java.lang.Math.round

class PostsFragment : MvpAppCompatFragment(), MainView {

    @InjectPresenter
    lateinit var mainPresenter: MainPresenter

    @ProvidePresenter
    fun providePresenter(): MainPresenter =
        Toothpick
            .openScope(APP_SCOPE)
            .getInstance(MainPresenter::class.java)

    private var _binding: FragmentPostsBinding? = null
    private val binding get() = _binding!!
    private val mAdapter by lazy {
        PostAdapter(
            mainPresenter::deletePost,
            mainPresenter::goToPostPage,
            mainPresenter::navigateToImage,
            mainPresenter::copyCommentToClipboard
        )
    }

    private lateinit var notificationManager: NotificationManagerCompat
    val channelId = "Progress Notification"

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostsBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        binding.postsRecyclerView.adapter = mAdapter
        binding.postsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        mainPresenter.checkGroupsExists()
        mainPresenter.setData()

        notificationManager = NotificationManagerCompat.from(requireContext())

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.posts_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.refresh_groups ->
                mainPresenter.insertIntoDb()
            R.id.refresh_comments ->
                mainPresenter.refreshComments()
            R.id.export_posts ->
                mainPresenter.exportPosts()
            R.id.import_posts ->
                mainPresenter.importPosts()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun sendToast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

    override fun setDataToRecycler(posts: List<PostEntity>) {
        mAdapter.setData(posts)
        binding.postsRecyclerView.scrollToPosition(mainPresenter.lastItem)
    }

    override fun goToPostPage(intent: Intent, bundle: Bundle) {
        ContextCompat.startActivity(requireContext(), intent, bundle)
    }

    override fun getPosition() {
        mainPresenter.lastItem = getCurrentItem(binding.postsRecyclerView)
    }

    override fun copyCommentToClipboard(comment: String) {
        val myClipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val myClip: ClipData = ClipData.newPlainText("Label", comment)
        myClipboard.setPrimaryClip(myClip)
        sendToast("$comment copied to clipboard")
    }

    private fun getCurrentItem(recyclerView: RecyclerView): Int {
        return (recyclerView.layoutManager as LinearLayoutManager)
            .findFirstVisibleItemPosition()
    }

    override fun createNotification(title: String) {
        val intent = Intent(requireContext(), MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(requireContext(), 0, intent, 0)

        val notification =
            NotificationCompat.Builder(requireContext(), channelId)
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
        mainPresenter.notification = notification
    }

    override fun updateNotification(message: String, progress: Int, maxProgress: Int) {

        if (progress != 0) {
            mainPresenter.notification.setContentText(message)
                .setProgress(maxProgress, progress, false)

            notificationManager.notify(1, mainPresenter.notification.build())
        } else {
            mainPresenter.notification
                .setContentText(message)
                .setProgress(0, 0, false)
                .setOngoing(false)
            notificationManager.notify(1, mainPresenter.notification.build())
        }
    }
}