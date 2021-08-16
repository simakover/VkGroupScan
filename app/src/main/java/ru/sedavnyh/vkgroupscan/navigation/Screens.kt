package ru.sedavnyh.vkgroupscan.navigation

import com.github.terrakok.cicerone.androidx.FragmentScreen
import ru.sedavnyh.vkgroupscan.fragments.ImageFragment
import ru.sedavnyh.vkgroupscan.fragments.PostsFragment

object Screens {
    fun postsScreen() = FragmentScreen { PostsFragment() }
    fun imageScreen(link: String) = FragmentScreen {ImageFragment().setImage(link)}
}