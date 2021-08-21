package ru.sedavnyh.vkgroupscan.adapters.groupAdapters

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
import ru.sedavnyh.vkgroupscan.adapters.CommentsAdapter
import ru.sedavnyh.vkgroupscan.models.entities.PostEntity
import ru.sedavnyh.vkgroupscan.util.PostDiffUtil

class AllGroupsAdapter(
    val onTitleClick: (PostEntity) -> Unit,
    val onDeleteClick: (PostEntity, Int) -> Unit,
    val onSnackBarUndo: (PostEntity) -> Unit,
    val onInnerCommentClick: (String) -> Unit,
    val onImageClick: (PostEntity) -> Unit
): RecyclerView.Adapter<AllGroupsAdapter.MyViewHolder>() {
    private var dataList = emptyList<PostEntity>()

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val cAdapter by lazy { CommentsAdapter(onCommentClick = onInnerCommentClick) }

        fun bind(post: PostEntity, position: Int) {

            itemView.title_text_view.text = post.groupName

            itemView.description_text_view.text = post.text
            itemView.description_text_view.fixTextSelection()

            itemView.group_avatar.load(post.groupAvatar) {
                transformations(CircleCropTransformation())
            }

            itemView.postImage.load(post.images[0])

            itemView.postImage.setOnClickListener {
                onImageClick.invoke(post)
            }

            itemView.title_text_view.setOnClickListener {
                onTitleClick.invoke(post)
            }

            itemView.group_avatar.setOnClickListener {
               onTitleClick.invoke(post)
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