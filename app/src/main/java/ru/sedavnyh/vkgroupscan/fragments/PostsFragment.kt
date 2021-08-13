package ru.sedavnyh.vkgroupscan.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.sedavnyh.vkgroupscan.R
import ru.sedavnyh.vkgroupscan.adapters.PostAdapter
import ru.sedavnyh.vkgroupscan.data.database.VkDao
import ru.sedavnyh.vkgroupscan.data.network.Api
import ru.sedavnyh.vkgroupscan.databinding.FragmentPostsBinding
import ru.sedavnyh.vkgroupscan.di.Scopes
import ru.sedavnyh.vkgroupscan.models.groupsModel.Group
import ru.sedavnyh.vkgroupscan.models.wallGetCommentsModel.Comment
import ru.sedavnyh.vkgroupscan.models.wallGetCommentsModel.RespondThread
import ru.sedavnyh.vkgroupscan.util.Constants.ACCESS_TOKEN
import ru.sedavnyh.vkgroupscan.util.Constants.API_VERSION
import ru.sedavnyh.vkgroupscan.util.Constants.THREAD_ITEMS_COUNT
import ru.sedavnyh.vkgroupscan.util.TextOperations
import toothpick.Toothpick
import javax.inject.Inject

class PostsFragment : Fragment() {

    @Inject
    lateinit var vkDao: VkDao

    @Inject
    lateinit var vkApi: Api

    private var _binding: FragmentPostsBinding? = null
    private val binding get() = _binding!!
    private val mAdapter by lazy { PostAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostsBinding.inflate(inflater, container, false)
        Toothpick.inject(this, Toothpick.openScope(Scopes.APP_SCOPE))
        setHasOptionsMenu(true)
        binding.postsRecyclerView.adapter = mAdapter
        binding.postsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        checkGroupsExists()

        vkDao.selectPosts().observe(viewLifecycleOwner, {
            mAdapter.setData(it)
        })

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
            R.id.refresh_comments ->
                refreshComments()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun refreshComments() {
        Log.d("inserting comment", "start")
        GlobalScope.launch(Dispatchers.Main) {
            vkDao.deleteComments()
            val posts = vkDao.selectPostsForComments()

            posts.map { post ->
                var summaryComment = ""
                val loadedComments = vkApi.wallGetComments(
                    ACCESS_TOKEN,
                    API_VERSION,
                    post.ownerId.toString(),
                    post.id.toString(),
                    THREAD_ITEMS_COUNT
                )
                Thread.sleep(500)
                loadedComments.response?.items?.map {

                    it.text = TextOperations().cleanComment(it.text)
                    if (it.text != null) {
                        Log.d("Add to summary", "${it.text}")
                        summaryComment = summaryComment.trim() + System.lineSeparator() + it.text
                    }
                    vkDao.insertComment(it)
                    it.respondThread?.items?.map { respComm ->
                        val comment = Comment(
                            respComm.id,
                            respComm.fromId,
                            respComm.ownerId,
                            respComm.postId,
                            respComm.text,
                            RespondThread(null)
                        )
                        respComm.text = TextOperations().cleanComment(respComm.text)
                        if (respComm.text != null) {
                            summaryComment = summaryComment.trim() + System.lineSeparator() + respComm.text
                        }
                        vkDao.insertComment(comment)
                    }
                }
                post.totalComments = summaryComment.trim()
                vkDao.insertPost(post)
            }
            Toast.makeText(requireContext(), "Done", Toast.LENGTH_SHORT).show()
        }
    }

    private fun insertIntoDb() {
        GlobalScope.launch(Dispatchers.Main) {
            val groups = vkDao.selectGroups()
            groups.map { group ->
                var response = vkApi.wallGet(ACCESS_TOKEN, API_VERSION, group.id.toString(), "1").response
                Thread.sleep(500)

                if (group.postCount < response?.count!!) {
                    var loadCount = response.count!! - group.postCount + 1

                    if (loadCount > 100)
                        loadCount = 100

                    group.postCount = response.count!!

                    response =
                        vkApi.wallGet(ACCESS_TOKEN, API_VERSION, group.id.toString(), loadCount.toString()).response
                    Thread.sleep(500)

                    response?.posts?.map { post ->
                        post.groupAvatar = group.avatar
                        post.groupName = group.title
                        vkDao.insertPost(post)
                    }
                    vkDao.updateGroup(group)
                }
            }
            Toast.makeText(requireContext(), "Done", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkGroupsExists() {
        GlobalScope.launch {
            val groups = vkDao.selectGroups()
            if (groups.isNullOrEmpty()) {
                var group = Group(
                    -140579116,
                    0,
                    "скрины из кетайских пopномультеков 0.2",
                    "https://sun1-25.userapi.com/s/v1/ig2/UL3xepuF-U7gpdwOLU8CBePLBJDMAu9QmtFw_QiDrBZg-B1LdPvv_bBeevZM3p5mEj2Cl4cM4VzCu-UQ-rEqnu-8.jpg?size=50x50&quality=96&crop=175,0,449,449&ava=1"
                )
                vkDao.insertGroup(group)

                group = Group(
                    -192370022,
                    0,
                    "a slice of doujin",
                    "https://sun1-13.userapi.com/s/v1/ig2/JtRDppZ2PqNu-rnWmxsqyvxDrOKqYTc3Jjkz_ChEV_c9grSMBZqL01TMacwfA7m5crENKZIZZUiUJBg0NqZkt5DH.jpg?size=50x50&quality=96&crop=104,4,908,908&ava=1"
                )
                vkDao.insertGroup(group)

                group = Group(
                    -184665352,
                    0,
                    "doujin cap",
                    "https://sun1-29.userapi.com/s/v1/ig2/5EmyxrOTvObLCoEfwb3ZDpb6ena0pPrwkpm37ga1bPOs-JN1rff8KQL7EiFNY1rGPobxvVHMSavfz3mAg2rDCNYs.jpg?size=50x50&quality=96&crop=106,0,426,426&ava=1"
                )
                vkDao.insertGroup(group)
            }
        }
    }
}