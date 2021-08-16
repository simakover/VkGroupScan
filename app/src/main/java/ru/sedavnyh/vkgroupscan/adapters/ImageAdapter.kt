package ru.sedavnyh.vkgroupscan.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import kotlinx.android.synthetic.main.image_row.view.*
import ru.sedavnyh.vkgroupscan.R
import ru.sedavnyh.vkgroupscan.util.LinksDiffUtil

class ImageAdapter : RecyclerView.Adapter<ImageAdapter.MyViewHolder>() {

    var dataList : List<String> = emptyList()

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(link: String) {
            itemView.pic_imageView.load(link)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setData(links: List<String>) {
        val postDiffUtil = LinksDiffUtil(dataList, links)
        val postDiffResult = DiffUtil.calculateDiff(postDiffUtil)
        this.dataList = links
        postDiffResult.dispatchUpdatesTo(this)
    }
}