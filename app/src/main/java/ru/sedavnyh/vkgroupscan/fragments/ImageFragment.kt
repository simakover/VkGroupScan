package ru.sedavnyh.vkgroupscan.fragments

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import ru.sedavnyh.vkgroupscan.R
import ru.sedavnyh.vkgroupscan.adapters.ImageAdapter
import ru.sedavnyh.vkgroupscan.databinding.FragmentImageBinding
import ru.sedavnyh.vkgroupscan.models.entities.PostEntity
import ru.sedavnyh.vkgroupscan.viewModels.GroupViewModel


class ImageFragment : Fragment() {

    private var _binding: FragmentImageBinding? = null
    private val binding get() = _binding!!

    private lateinit var activity : AppCompatActivity

    private val args: ImageFragmentArgs by navArgs()

    private lateinit var post : PostEntity

    lateinit var viewModel: GroupViewModel

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentImageBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(GroupViewModel::class.java)

        post = args.postEntityArg

        val adapter = ImageAdapter()
        binding.imageViewViewPager.adapter = adapter
        adapter.setData(post.images)

        activity  = requireActivity() as AppCompatActivity

        binding.imageViewViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                activity.supportActionBar?.title = "${position + 1} из ${post.images.size}"
            }
        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity.setSupportActionBar(binding.imageToolbar)
        setHasOptionsMenu(true)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.window.statusBarColor = ContextCompat.getColor(activity,R.color.black)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.images_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> findNavController().navigate(R.id.action_imageFragment_to_viewPagerFragment2)
            R.id.search_image -> {
                val link = post.images[binding.imageViewViewPager.currentItem]
                findImage(link)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun findImage(link: String) {
        viewModel.findImage(link)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        activity.window.statusBarColor = ContextCompat.getColor(activity, R.color.purple_700)
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