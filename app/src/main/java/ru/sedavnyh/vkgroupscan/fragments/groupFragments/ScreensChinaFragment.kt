package ru.sedavnyh.vkgroupscan.fragments.groupFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import ru.sedavnyh.vkgroupscan.R
import ru.sedavnyh.vkgroupscan.adapters.groupAdapters.DoujinCapAdapter
import ru.sedavnyh.vkgroupscan.adapters.groupAdapters.ScreensChinaAdapter
import ru.sedavnyh.vkgroupscan.databinding.FragmentDoujinCapBinding
import ru.sedavnyh.vkgroupscan.databinding.FragmentScreensChinaBinding
import ru.sedavnyh.vkgroupscan.di.Scopes
import ru.sedavnyh.vkgroupscan.viewModels.GroupViewModel
import toothpick.Toothpick

class ScreensChinaFragment : Fragment() {
    lateinit var viewModel: GroupViewModel
    lateinit var adapter: ScreensChinaAdapter

    private var _binding: FragmentScreensChinaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScreensChinaBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        Toothpick.inject(this, Toothpick.openScope(Scopes.APP_SCOPE))
        viewModel = ViewModelProvider(this).get(GroupViewModel::class.java)

        adapter = ScreensChinaAdapter(viewModel::goToPostPage, viewModel::deletePost, viewModel::insertPost, viewModel::copyCommentToClipboard)
        binding.ScreensChinaRecyclerView.adapter = adapter
        binding.ScreensChinaRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        setObserver()

        return binding.root
    }

    override fun toString(): String {
        return "Кейтайские порномультики"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.refresh_comments -> {
                viewModel.refreshComments(-140579116)
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
        viewModel.getData(-140579116).removeObservers(viewLifecycleOwner)
    }

    private fun setObserver() {
        viewModel.getData(-140579116).observe(viewLifecycleOwner, {
            adapter.setData(it)
            binding.ScreensChinaRecyclerView.scrollToPosition(viewModel.lastItem)
        })
    }
}