package ru.sedavnyh.vkgroupscan.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.sedavnyh.vkgroupscan.R
import ru.sedavnyh.vkgroupscan.data.database.VkDao
import ru.sedavnyh.vkgroupscan.data.network.Api
import ru.sedavnyh.vkgroupscan.databinding.FragmentPostsBinding
import ru.sedavnyh.vkgroupscan.di.Scopes
import ru.sedavnyh.vkgroupscan.models.groupsModel.Group
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
        setHasOptionsMenu(true)

        GlobalScope.launch {
            val groups = vkDao.selectGroups()
            if (groups.isNullOrEmpty()) {
                var group = Group(-140579116, 15900)
                vkDao.insertGroup(group)
            }
        }

        binding.selectButton.setOnClickListener {
            selectFromDb()
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.posts_menu, menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.refresh_groups ->
                insertIntoDb()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun insertIntoDb() {
        GlobalScope.launch {
            val groups = vkDao.selectGroups()
            groups.map {
                var response = vkApi.wallGet(ACCESS_TOKEN, API_VERSION, it.id.toString(), "1").response
                Thread.sleep(1000)

                if (it.postCount < response?.count!!) {
                    var loadCount = response.count!! - it.postCount
                    it.postCount = response.count!!

                    response = vkApi.wallGet(ACCESS_TOKEN, API_VERSION, it.id.toString(), loadCount.toString()).response
                    Thread.sleep(1000)

                    response?.posts?.map { post ->
                        Log.d("inserting", "${post.id}")
                        vkDao.insertPost(post)
                    }

                    vkDao.updateGroup(it)
                }
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