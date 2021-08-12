package ru.sedavnyh.vkgroupscan.navigation

import com.github.terrakok.cicerone.androidx.FragmentScreen
import ru.sedavnyh.vkgroupscan.fragments.PostsFragment

object Screens {
    fun PostsScreen() = FragmentScreen { PostsFragment() }
}