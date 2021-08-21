package ru.sedavnyh.vkgroupscan.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.sedavnyh.vkgroupscan.R
import ru.sedavnyh.vkgroupscan.models.entities.GroupEntity
import ru.sedavnyh.vkgroupscan.util.GroupStringDiffUtil

class ViewPagerAdapterOld: RecyclerView.Adapter<ViewPagerAdapterOld.MyViewHolder>() {

    var dataList : List<GroupEntity> = emptyList()

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(group: GroupEntity) {
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view_pager, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setData(links: List<GroupEntity>) {
        val commentDiffUtil = GroupStringDiffUtil(dataList, links)
        val postDiffResult = DiffUtil.calculateDiff(commentDiffUtil)
        this.dataList = links
        postDiffResult.dispatchUpdatesTo(this)
    }
}