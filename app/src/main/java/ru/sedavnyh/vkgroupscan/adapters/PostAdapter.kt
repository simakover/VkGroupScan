package ru.sedavnyh.vkgroupscan.adapters

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import kotlinx.android.synthetic.main.post_row.view.*
import ru.sedavnyh.vkgroupscan.R
import ru.sedavnyh.vkgroupscan.models.entities.PostEntity
import ru.sedavnyh.vkgroupscan.util.PostDiffUtil

class PostAdapter(
    val onDeleteClick: (PostEntity) -> Unit,
    val onTitleClick: (PostEntity) -> Unit,
    val onInnerImageClick: (String) -> Unit,
) : RecyclerView.Adapter<PostAdapter.MyViewHolder>() {
    private var dataList = emptyList<PostEntity>()

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val iAdapter by lazy { ImageAdapter(onImageClick = onInnerImageClick) }

        fun bind(post: PostEntity) {

            itemView.title_text_view.text = post.groupName

            itemView.description_text_view.text = post.text
            itemView.description_text_view.fixTextSelection()

            var summaryComment = ""
            post.totalComments.map {
                summaryComment = summaryComment.trim() + System.lineSeparator() + it
            }
            itemView.founded_links.text = summaryComment
            itemView.founded_links.fixTextSelection()

            itemView.group_avatar.load(post.groupAvatar) {
                transformations(CircleCropTransformation())
            }
            itemView.title_text_view.setOnClickListener {
                onTitleClick.invoke(post)
            }

            itemView.group_avatar.setOnClickListener {
                onTitleClick.invoke(post)
            }

            itemView.delete_post_button.setOnClickListener {
                onDeleteClick.invoke(post)
            }

            itemView.image_recyclerView.adapter = iAdapter
            itemView.image_recyclerView.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
            iAdapter.setData(post.images)
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

    fun setData(posts: List<PostEntity>) {
        val postDiffUtil = PostDiffUtil(dataList, posts)
        val postDiffResult = DiffUtil.calculateDiff(postDiffUtil)
        this.dataList = posts
        postDiffResult.dispatchUpdatesTo(this)
    }
}