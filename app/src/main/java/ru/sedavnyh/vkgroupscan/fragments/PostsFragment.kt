package ru.sedavnyh.vkgroupscan.fragments

import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.sedavnyh.vkgroupscan.R
import ru.sedavnyh.vkgroupscan.adapters.PostAdapter
import ru.sedavnyh.vkgroupscan.databinding.FragmentPostsBinding
import ru.sedavnyh.vkgroupscan.di.Scopes.APP_SCOPE
import ru.sedavnyh.vkgroupscan.models.entities.PostEntity
import ru.sedavnyh.vkgroupscan.models.wallGetModel.Post
import ru.sedavnyh.vkgroupscan.presenters.MainPresenter
import ru.sedavnyh.vkgroupscan.view.MainView
import toothpick.Toothpick

class PostsFragment : MvpAppCompatFragment(), MainView {

    @InjectPresenter
    lateinit var mainPresenter: MainPresenter

    @ProvidePresenter
    fun providePresenter() : MainPresenter =
        Toothpick
            .openScope(APP_SCOPE)
            .getInstance(MainPresenter::class.java)


    private var _binding: FragmentPostsBinding? = null
    private val binding get() = _binding!!
    private val mAdapter by lazy { PostAdapter(mainPresenter::deletePost) }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostsBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        binding.postsRecyclerView.adapter = mAdapter
        binding.postsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        mainPresenter.checkGroupsExists()
        mainPresenter.setData()

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
                mainPresenter.insertIntoDb()
            R.id.refresh_comments ->
                mainPresenter.refreshComments()
            R.id.export_posts ->
                mainPresenter.exportPosts()
            R.id.import_posts ->
                mainPresenter.importPosts()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun sendToast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

    override fun setDataToRecycler(posts: List<PostEntity>) {
        val lastItem = getCurrentItem(binding.postsRecyclerView)
        mAdapter.setData(posts)
        binding.postsRecyclerView.scrollToPosition(lastItem + 1)
    }

    private fun getCurrentItem(recyclerView: RecyclerView): Int {
        return (recyclerView.layoutManager as LinearLayoutManager)
            .findFirstVisibleItemPosition()
    }
}