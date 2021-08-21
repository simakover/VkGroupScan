package ru.sedavnyh.vkgroupscan.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.google.android.material.tabs.TabLayout
import ru.sedavnyh.vkgroupscan.R
import ru.sedavnyh.vkgroupscan.adapters.ImageAdapter
import ru.sedavnyh.vkgroupscan.databinding.FragmentImageBinding

class ImageFragment : Fragment() {

    private var _binding: FragmentImageBinding? = null
    private val binding get() = _binding!!

    private lateinit var activity : AppCompatActivity
    private var uiHiden = false
    lateinit var tabLayout : TabLayout

    val args: ImageFragmentArgs by navArgs()

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentImageBinding.inflate(inflater, container, false)

        val posts = args.postEntityArg

        val adapter = ImageAdapter()
        binding.imageViewRecyclerView.adapter = adapter
        binding.imageViewRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        adapter.setData(posts.images)

        tabLayout = requireActivity().findViewById(R.id.viewPagerTabsLayout)!!

        activity  = requireActivity() as AppCompatActivity
        hideSystemUI()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        showSystemUI()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun hideSystemUI() {
        uiHiden = true
        tabLayout.visibility = View.GONE
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }
    private fun showSystemUI() {
        uiHiden = false
        tabLayout.visibility = View.VISIBLE
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }
}