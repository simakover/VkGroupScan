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
import com.google.android.material.tabs.TabLayout
import ru.sedavnyh.vkgroupscan.R
import ru.sedavnyh.vkgroupscan.adapters.ImageAdapter
import ru.sedavnyh.vkgroupscan.databinding.FragmentImageBinding

class ImageFragment : Fragment() {

    private var _binding: FragmentImageBinding? = null
    private val binding get() = _binding!!

    private lateinit var activity : AppCompatActivity
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
        binding.imageViewViewPager.adapter = adapter
        adapter.setData(posts.images)

        tabLayout = requireActivity().findViewById(R.id.viewPagerTabsLayout)!!

        activity  = requireActivity() as AppCompatActivity
//        hideSystemUI()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        showSystemUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun hideSystemUI() {
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }
    private fun showSystemUI() {
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }
}