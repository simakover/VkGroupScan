package ru.sedavnyh.vkgroupscan.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_view_pager.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.sedavnyh.vkgroupscan.R
import ru.sedavnyh.vkgroupscan.adapters.ViewPagerAdapter
import ru.sedavnyh.vkgroupscan.models.entities.GroupEntity
import ru.sedavnyh.vkgroupscan.util.Constants
import ru.sedavnyh.vkgroupscan.viewModels.ViewPagerViewModel

class ViewPagerFragment : Fragment() {

    lateinit var mainViewModel: ViewPagerViewModel
    private val fragmentList: ArrayList<Fragment> = mutableListOf<Fragment>() as ArrayList<Fragment>
    private lateinit var mSort: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_view_pager, container, false)

        mainViewModel = ViewModelProvider(this).get(ViewPagerViewModel::class.java)
        mSort = requireContext().getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE)

        GlobalScope.launch(Dispatchers.Main) {
            val groups = mainViewModel.repository.local.selectGroups()
            fragmentList.add(GroupFragment(GroupEntity(0, 0, "All", "")))
            groups.map {
                fragmentList.add(GroupFragment(it))
            }
            val adapter = ViewPagerAdapter(
                fragmentList,
                requireActivity().supportFragmentManager,
                lifecycle
            )
            view.viewPager.adapter = adapter
            TabLayoutMediator(view.viewPagerTabsLayout, view.viewPager) { tab, position ->
                tab.text = fragmentList[position].toString()
            }.attach()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (context as AppCompatActivity).setSupportActionBar(view.viewPagerToolbar)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.posts_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.refresh_groups ->
                mainViewModel.insertIntoDb()
            R.id.menu_date_desc -> {
                mSort.edit().putString(Constants.APP_PREFERENCE_SORT, "DESC").apply()
                fragmentList.map {
                    (it as GroupFragment).setObserverWithPriority("DESC")
                }
            }
            R.id.menu_date_asc -> {
                mSort.edit().putString(Constants.APP_PREFERENCE_SORT, "ASC").apply()
                fragmentList.map {
                    (it as GroupFragment).setObserverWithPriority("ASC")
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}