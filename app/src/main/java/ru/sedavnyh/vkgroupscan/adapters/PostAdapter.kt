package ru.sedavnyh.vkgroupscan.adapters

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import kotlinx.android.synthetic.main.post_row.view.*
import ru.sedavnyh.vkgroupscan.R
import ru.sedavnyh.vkgroupscan.models.entities.PostEntity
import ru.sedavnyh.vkgroupscan.models.wallGetModel.Post
import ru.sedavnyh.vkgroupscan.util.PostDiffUtil

class PostAdapter(
    val onDeleteClick: (Post) -> Unit
) : RecyclerView.Adapter<PostAdapter.MyViewHolder>() {
    private var dataList = emptyList<Post>()

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(post: Post) {

            itemView.title_text_view.text = post.groupName

            itemView.description_text_view.text = post.text
            itemView.description_text_view.fixTextSelection()

            itemView.founded_links.text = post.totalComments
            itemView.founded_links.fixTextSelection()

            itemView.group_avatar.load(post.groupAvatar)
            itemView.title_text_view.setOnClickListener {
                val uris = Uri.parse("https://vk.com/wall${post.ownerId}_${post.id}")
                val intents = Intent(Intent.ACTION_VIEW, uris)
                val b = Bundle()
                b.putBoolean("new_window", true)
                intents.putExtras(b)
                startActivity(itemView.context, intents, b)
            }

            itemView.delete_post_button.setOnClickListener {
                onDeleteClick.invoke(post)
            }

            try {
                post.attachments?.first()?.photo?.sizes?.map {
                    if (it.type == "x") {
                        itemView.image1.load(it.url)
                    }
                }
            } catch (e: Exception) {
                Log.d("exception", e.toString())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostAdapter.MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
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