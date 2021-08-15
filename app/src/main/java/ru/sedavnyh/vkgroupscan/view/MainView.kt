package ru.sedavnyh.vkgroupscan.view

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.sedavnyh.vkgroupscan.models.wallGetModel.Post

@StateStrategyType(value = AddToEndSingleStrategy::class)
interface MainView: MvpView {
    fun sendToast(text: String)
    fun setDataToRecycler(posts: List<Post>)
}