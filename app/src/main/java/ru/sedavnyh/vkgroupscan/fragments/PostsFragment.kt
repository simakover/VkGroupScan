package ru.sedavnyh.vkgroupscan.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.sedavnyh.vkgroupscan.data.database.VkDao
import ru.sedavnyh.vkgroupscan.data.network.Api
import ru.sedavnyh.vkgroupscan.databinding.FragmentPostsBinding
import ru.sedavnyh.vkgroupscan.di.Scopes
import ru.sedavnyh.vkgroupscan.util.Constants.ACCESS_TOKEN
import ru.sedavnyh.vkgroupscan.util.Constants.API_VERSION
import toothpick.Toothpick
import javax.inject.Inject

class PostsFragment : Fragment() {

    private var _binding: FragmentPostsBinding? = null
    private val binding get() =_binding!!

    @Inject
    lateinit var vkDao : VkDao

    @Inject
    lateinit var vkApi : Api

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostsBinding.inflate(inflater, container, false)

        Toothpick.inject(this, Toothpick.openScope(Scopes.APP_SCOPE))

        binding.selectButton.setOnClickListener {
            selectFromDb()
        }

        binding.insertButton.setOnClickListener {
            insertIntoDb()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun insertIntoDb() {
        GlobalScope.launch {
            vkApi.wallGet(ACCESS_TOKEN, API_VERSION,"-140579116").response?.posts?.map { post ->
                Log.d("inserting", "${post.id}")
                vkDao.insertPost(post)
            }
        }
    }

    private fun selectFromDb() {
        GlobalScope.launch {
            val posts = vkDao.selectPosts()
            Log.d("selectFromDb", "${posts.size}")
        }
    }
}