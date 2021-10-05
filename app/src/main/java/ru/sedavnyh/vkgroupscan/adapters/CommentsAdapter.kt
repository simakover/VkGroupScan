package ru.sedavnyh.vkgroupscan.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.string_row.view.*
import ru.sedavnyh.vkgroupscan.R
import ru.sedavnyh.vkgroupscan.util.BasicDiffUtil


class CommentsAdapter(
    val goToTach: (String) -> Unit,
    val copyToClipboard: (String) -> Unit
) : RecyclerView.Adapter<CommentsAdapter.MyViewHolder>() {

    var dataList : List<String> = emptyList()

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(comment: String) {
            itemView.textRow_textView.text = comment
            itemView.goToTach_button.setOnClickListener {
                goToTach.invoke(comment)
            }
            itemView.copytoClipboard_button.setOnClickListener {
                copyToClipboard.invoke(comment)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.string_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setData(links: List<String>) {
        val commentDiffUtil = BasicDiffUtil(dataList, links)
        val commentDiffResult = DiffUtil.calculateDiff(commentDiffUtil)
        this.dataList = links
        commentDiffResult.dispatchUpdatesTo(this)
    }
}