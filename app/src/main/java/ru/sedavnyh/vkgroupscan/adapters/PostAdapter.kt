package ru.sedavnyh.vkgroupscan.adapters

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.sedavnyh.vkgroupscan.data.database.VkDao
import ru.sedavnyh.vkgroupscan.databinding.PostRowBinding
import ru.sedavnyh.vkgroupscan.di.Scopes
import ru.sedavnyh.vkgroupscan.models.wallGetModel.Post
import ru.sedavnyh.vkgroupscan.util.PostDiffUtil
import toothpick.Toothpick
import javax.inject.Inject

class PostAdapter : RecyclerView.Adapter<PostAdapter.MyViewHolder>() {

    @Inject
    lateinit var vkDao: VkDao
    private var dataList = emptyList<Post>()

    class MyViewHolder(private val binding: PostRowBinding) :

        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post, dao : VkDao) {

            binding.titleTextView.text = post.groupName
            binding.descriptionTextView.text = post.text
            binding.foundedLinks.text = post.totalComments
            binding.groupAvatar.load(post.groupAvatar)
            binding.titleTextView.setOnClickListener {
                val uris = Uri.parse("https://vk.com/wall${post.ownerId}_${post.id}")
                val intents = Intent(Intent.ACTION_VIEW, uris)
                val b = Bundle()
                b.putBoolean("new_window", true)
                intents.putExtras(b)
                startActivity(binding.root.context, intents,b)
            }
            binding.deletePostButton.setOnClickListener{
                GlobalScope.launch {
                    dao.deletePost(post)
                }
            }

            post.attachments?.first()?.photo?.sizes?.map {
                if (it.type == "x") {
                    binding.image1.load(it.url)
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = PostRowBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Toothpick.inject(this, Toothpick.openScope(Scopes.APP_SCOPE))
        holder.bind(dataList[position], vkDao)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setData(posts: List<Post>) {
        val repoDiffUtil = PostDiffUtil(dataList, posts)
        val toDoDiffResult = DiffUtil.calculateDiff(repoDiffUtil)
        this.dataList = posts
        toDoDiffResult.dispatchUpdatesTo(this)
    }
}