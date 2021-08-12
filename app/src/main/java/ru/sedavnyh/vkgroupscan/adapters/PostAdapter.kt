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
import ru.sedavnyh.vkgroupscan.databinding.PostRowBinding
import ru.sedavnyh.vkgroupscan.models.wallGetModel.Post
import ru.sedavnyh.vkgroupscan.util.PostDiffUtil

class PostAdapter : RecyclerView.Adapter<PostAdapter.MyViewHolder>() {

    private var dataList = emptyList<Post>()

    class MyViewHolder(private val binding: PostRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            binding.titleTextView.text = post.GroupName
            binding.descriptionTextView.text = post.text
            binding.groupAvatar.load(post.GroupAvatar)
            binding.titleTextView.setOnClickListener {
                val uris = Uri.parse("https://vk.com/wall${post.ownerId}_${post.id}")
                val intents = Intent(Intent.ACTION_VIEW, uris)
                val b = Bundle()
                b.putBoolean("new_window", true)
                intents.putExtras(b)
                startActivity(binding.root.context, intents,b)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostAdapter.MyViewHolder {
        return PostAdapter.MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: PostAdapter.MyViewHolder, position: Int) {
        holder.bind(dataList[position])
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