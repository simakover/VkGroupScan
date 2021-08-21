package ru.sedavnyh.vkgroupscan.fragments.groupFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ru.sedavnyh.vkgroupscan.R
import ru.sedavnyh.vkgroupscan.adapters.groupAdapters.AllGroupsAdapter
import ru.sedavnyh.vkgroupscan.databinding.FragmentAllGroupsBinding
import ru.sedavnyh.vkgroupscan.di.Scopes
import ru.sedavnyh.vkgroupscan.fragments.ViewPagerFragmentDirections
import ru.sedavnyh.vkgroupscan.models.entities.PostEntity
import ru.sedavnyh.vkgroupscan.viewModels.GroupViewModel
import toothpick.Toothpick

class AllGroupsFragment : Fragment() {

    lateinit var viewModel: GroupViewModel
    lateinit var adapter: AllGroupsAdapter

    private var _binding: FragmentAllGroupsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAllGroupsBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        Toothpick.inject(this, Toothpick.openScope(Scopes.APP_SCOPE))
        viewModel = ViewModelProvider(this).get(GroupViewModel::class.java)

        adapter = AllGroupsAdapter(viewModel::goToPostPage, viewModel::deletePost, viewModel::insertPost, viewModel::copyCommentToClipboard, ::goToImageFragment)
        binding.allGroupsRecyclerView.adapter = adapter
        binding.allGroupsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        setObserver()

        return binding.root
    }

    override fun toString(): String {
        return "All"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.refresh_comments -> {
                viewModel.refreshComments(0)
            }
            R.id.menu_date_desc -> {
                removeObserver()
                viewModel.setSortOrder("DESC")
                setObserver()
            }
            R.id.menu_date_asc -> {
                removeObserver()
                viewModel.setSortOrder("ASC")
                setObserver()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun removeObserver() {
        viewModel.getData(0).removeObservers(viewLifecycleOwner)
    }

    private fun setObserver() {
        viewModel.getData(0).observe(viewLifecycleOwner, {
            adapter.setData(it)
            binding.allGroupsRecyclerView.scrollToPosition(viewModel.lastItem)
        })
    }

    fun goToImageFragment(postEntity: PostEntity) {

        val action = ViewPagerFragmentDirections.actionViewPagerFragmentToImageFragment(postEntity)
        findNavController().navigate(action)
    }
}