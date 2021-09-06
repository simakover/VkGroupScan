package ru.sedavnyh.vkgroupscan.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import kotlinx.android.synthetic.main.find_image_row.view.*
import ru.sedavnyh.vkgroupscan.R
import ru.sedavnyh.vkgroupscan.models.entities.FindImageEntity
import ru.sedavnyh.vkgroupscan.util.BasicDiffUtil

class FindImageAdapter(
    val onTextClick: (String) -> Unit
) : RecyclerView.Adapter<FindImageAdapter.MyViewHolder>() {

    private var dataList = emptyList<FindImageEntity>()

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(image: FindImageEntity) {
            itemView.find_imageView.load(image.link) {
                crossfade(true)
                placeholder(R.drawable.ic_broken_image)
            }
            itemView.find_textView.text = image.title
            itemView.find_textView.setOnClickListener {
                onTextClick.invoke(image.title)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.find_image_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setData(images: List<FindImageEntity>) {
        val postDiffUtil = BasicDiffUtil(dataList, images)
        val postDiffResult = DiffUtil.calculateDiff(postDiffUtil)
        this.dataList = images
        postDiffResult.dispatchUpdatesTo(this)
    }
}