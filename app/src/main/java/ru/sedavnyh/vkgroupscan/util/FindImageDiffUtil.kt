package ru.sedavnyh.vkgroupscan.util

import androidx.recyclerview.widget.DiffUtil
import ru.sedavnyh.vkgroupscan.models.entities.FindImageEntity

class FindImageDiffUtil(
    private val oldList: List<FindImageEntity>,
    private val newList: List<FindImageEntity>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}