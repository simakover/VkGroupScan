package ru.sedavnyh.vkgroupscan.view

import android.content.Intent
import android.os.Bundle
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.sedavnyh.vkgroupscan.models.entities.PostEntity

@StateStrategyType(value = AddToEndSingleStrategy::class)
interface MainView: MvpView {
    fun sendToast(text: String)
    fun setDataToRecycler(posts: List<PostEntity>)
    fun goToPostPage(intent : Intent, bundle: Bundle)
    fun getPosition()
    fun copyCommentToClipboard(comment: String)
}