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
import ru.sedavnyh.vkgroupscan.util.Constants
import ru.sedavnyh.vkgroupscan.viewModels.ViewPagerViewModel

class ViewPagerFragment : Fragment() {

    lateinit var mainViewModel: ViewPagerViewModel
    private var fragmentList: ArrayList<Fragment> = mutableListOf<Fragment>() as ArrayList<Fragment>
    private lateinit var mSort: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_view_pager, container, false)

        mainViewModel = ViewModelProvider(this).get(ViewPagerViewModel::class.java)
        mSort = requireContext().getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE)

        GlobalScope.launch(Dispatchers.Main) {
            fragmentList = mainViewModel.getGroupFragments()
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
            R.id.menu_date_desc ->
                setSortOrder("DESC")
            R.id.menu_date_asc ->
                setSortOrder("ASC")
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setSortOrder(sortOrder: String) {
        mSort.edit().putString(Constants.APP_PREFERENCE_SORT, sortOrder).apply()
        fragmentList.map {
            (it as GroupFragment).setObserverWithPriority(sortOrder)
        }
    }
}