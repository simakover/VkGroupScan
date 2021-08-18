package ru.sedavnyh.vkgroupscan.fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_posts.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.sedavnyh.vkgroupscan.R
import ru.sedavnyh.vkgroupscan.adapters.PostAdapter
import ru.sedavnyh.vkgroupscan.adapters.ViewPagerAdapter
import ru.sedavnyh.vkgroupscan.data.Repository
import ru.sedavnyh.vkgroupscan.databinding.FragmentPostsBinding
import ru.sedavnyh.vkgroupscan.di.Scopes.APP_SCOPE
import ru.sedavnyh.vkgroupscan.models.entities.GroupEntity
import ru.sedavnyh.vkgroupscan.models.entities.PostEntity
import ru.sedavnyh.vkgroupscan.presenters.MainPresenter
import ru.sedavnyh.vkgroupscan.util.Constants
import ru.sedavnyh.vkgroupscan.util.Constants.APP_PREFERENCES
import ru.sedavnyh.vkgroupscan.view.MainView
import toothpick.Toothpick
import javax.inject.Inject

class PostsFragment : MvpAppCompatFragment(), MainView {

    @InjectPresenter
    lateinit var mainPresenter: MainPresenter

    @ProvidePresenter
    fun providePresenter(): MainPresenter =
        Toothpick
            .openScope(APP_SCOPE)
            .getInstance(MainPresenter::class.java)

    private var _binding: FragmentPostsBinding? = null
    private val binding get() = _binding!!
    private val mAdapter by lazy {
        PostAdapter(
            mainPresenter::deletePost,
            mainPresenter::goToPostPage,
            mainPresenter::navigateToImage,
            mainPresenter::copyCommentToClipboard
        )
    }
    private val vpAdapter by lazy { ViewPagerAdapter() }
    lateinit var mSort: SharedPreferences

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostsBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        binding.postsRecyclerView.adapter = mAdapter
        binding.postsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.vp2.adapter = vpAdapter
        mSort = requireContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)

        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                mSort.edit().putInt(Constants.APP_PREFERENCE_SORT_GROUP, tab.id).apply()
                mainPresenter.setData()
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        val activity  = requireActivity() as AppCompatActivity

        binding.postsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    activity.supportActionBar?.hide()
                } else {
                    activity.supportActionBar?.show()
                }
            }
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
                mainPresenter.insertIntoDb()
            R.id.refresh_comments ->
                mainPresenter.refreshComments()
            R.id.menu_date_desc -> {
                mainPresenter.setSortOrder("DESC")
                mainPresenter.setData()
            }
            R.id.menu_date_asc -> {
                mainPresenter.setSortOrder("ASC")
                mainPresenter.setData()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setDataToRecycler(posts: List<PostEntity>) {
        mAdapter.setData(posts)
        binding.postsRecyclerView.scrollToPosition(mainPresenter.lastItem)
    }

    override fun setDataToViewPager(groups: MutableList<GroupEntity>) {
        vpAdapter.setData(groups)
        TabLayoutMediator(binding.tabLayout, binding.vp2) { tab, position ->
            tab.text = groups[position].title
            tab.id = groups[position].id
        }.attach()
    }

    override fun copyCommentToClipboard(comment: String) {
        val myClipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val myClip: ClipData = ClipData.newPlainText("Label", comment)
        myClipboard.setPrimaryClip(myClip)
        Toast.makeText(requireContext(), "$comment copied to clipboard", Toast.LENGTH_SHORT).show()
    }
}