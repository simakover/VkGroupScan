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
import coil.load
import ru.sedavnyh.vkgroupscan.databinding.FragmentImageBinding

class ImageFragment : Fragment() {

    private var _binding: FragmentImageBinding? = null
    private val binding get() = _binding!!
    private var linkImage: String = ""
    private lateinit var activity : AppCompatActivity
    private var uiHiden = false

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentImageBinding.inflate(inflater, container, false)
        binding.fullImageImageView.load(linkImage)

        activity  = requireActivity() as AppCompatActivity
        activity.supportActionBar?.hide()
        hideSystemUI()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        showSystemUI()
        activity.supportActionBar?.show()
    }

    fun setImage(link: String) : ImageFragment{
        linkImage = link
        return this
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun hideSystemUI() {
        uiHiden = true
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }
    private fun showSystemUI() {
        uiHiden = false
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }
}