package ru.sedavnyh.vkgroupscan.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_view_pager.view.*
import ru.sedavnyh.vkgroupscan.R
import ru.sedavnyh.vkgroupscan.adapters.ViewPagerAdapter
import ru.sedavnyh.vkgroupscan.fragments.groupFragments.AllGroupsFragment
import ru.sedavnyh.vkgroupscan.fragments.groupFragments.DoujinCapFragment
import ru.sedavnyh.vkgroupscan.fragments.groupFragments.ScreensChinaFragment
import ru.sedavnyh.vkgroupscan.fragments.groupFragments.SliceDoujinFragment
import ru.sedavnyh.vkgroupscan.viewModels.ViewPagerViewModel

class ViewPagerFragment : Fragment() {

    lateinit var mainViewModel : ViewPagerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_view_pager, container, false)

        val fragmentList = arrayListOf<Fragment>(
            AllGroupsFragment(),
            SliceDoujinFragment(),
            DoujinCapFragment(),
            ScreensChinaFragment()
        )

        val adapter = ViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )
        view.viewPager.adapter = adapter
        TabLayoutMediator(view.viewPagerTabsLayout, view.viewPager) { tab, position ->
            tab.text = fragmentList[position].toString()
        }.attach()

        mainViewModel = ViewModelProvider(this).get(ViewPagerViewModel::class.java)

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
        }
        return super.onOptionsItemSelected(item)
    }
}