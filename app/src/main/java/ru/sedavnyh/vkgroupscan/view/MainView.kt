package ru.sedavnyh.vkgroupscan.view

import android.content.Intent
import android.os.Bundle
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.sedavnyh.vkgroupscan.models.entities.GroupEntity
import ru.sedavnyh.vkgroupscan.models.entities.PostEntity

@StateStrategyType(value = AddToEndSingleStrategy::class)
interface MainView: MvpView {
    fun setDataToRecycler(posts: List<PostEntity>)
    fun setDataToViewPager(groups : MutableList<GroupEntity>)
    fun copyCommentToClipboard(comment: String)
}