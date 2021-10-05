package ru.sedavnyh.vkgroupscan.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.post_row.view.*
import ru.sedavnyh.vkgroupscan.R
import ru.sedavnyh.vkgroupscan.models.entities.PostEntity
import ru.sedavnyh.vkgroupscan.util.PostDiffUtil

class GroupAdapter(
    val onOpenVKClick: (PostEntity) -> Unit,
    val onDeleteClick: (PostEntity, Int) -> Unit,
    val onSnackBarUndo: (PostEntity) -> Unit,
    val goToTachClick: (String) -> Unit,
    val copyToClipboardClick: (String) -> Unit,
    val onImageClick: (PostEntity) -> Unit
): RecyclerView.Adapter<GroupAdapter.MyViewHolder>() {
    private var dataList = emptyList<PostEntity>()

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val cAdapter by lazy { CommentsAdapter(goToTachClick, copyToClipboardClick) }

        fun bind(post: PostEntity, position: Int) {

            itemView.title_text_view.text = post.groupName

            itemView.group_avatar.load(post.groupAvatar) {
                transformations(CircleCropTransformation())
            }
            try {
                itemView.postImage.load(post.images.first()) {
                    crossfade(true)
                    placeholder(R.drawable.ic_broken_image)
                }
            } catch (e: Exception) {
                itemView.postImage.load(R.drawable.ic_broken_image)
            }

            itemView.postImage.setOnClickListener {
                onImageClick.invoke(post)
            }

            itemView.delete_post_button.setOnClickListener {
                onDeleteClick.invoke(post, position)

                val snackBar = Snackbar.make(
                    itemView, "Удален пост '${post.id}'", Snackbar.LENGTH_LONG
                )
                snackBar.setAction("Восстановить") {
                    onSnackBarUndo.invoke(post)
                }
                snackBar.show()
            }

            itemView.openVK_post_button.setOnClickListener {
                onOpenVKClick.invoke(post)
            }

            itemView.founded_links_recyclerView.adapter = cAdapter
            itemView.founded_links_recyclerView.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
            cAdapter.setData(post.totalComments)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(dataList[position], position)
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