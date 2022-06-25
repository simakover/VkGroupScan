package ru.sedavnyh.vkgroupscan.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import ru.sedavnyh.vkgroupscan.R
import ru.sedavnyh.vkgroupscan.adapters.FindImageAdapter
import ru.sedavnyh.vkgroupscan.databinding.FragmentFindImageBinding
import ru.sedavnyh.vkgroupscan.models.entities.FindImageEntity
import ru.sedavnyh.vkgroupscan.util.TextOperations
import ru.sedavnyh.vkgroupscan.viewModels.GroupViewModel

class FindImageFragment : Fragment() {

    private var _binding: FragmentFindImageBinding? = null
    private val binding get() = _binding!!

    lateinit var viewModel: GroupViewModel

    private val args: FindImageFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFindImageBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(GroupViewModel::class.java)

        val adapter = FindImageAdapter(viewModel::openTachiyomiWithQuery, viewModel::copyCommentToClipboard)
        binding.FindImageRecyclerView.adapter = adapter
        binding.FindImageRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val linkToImage = args.linkToImage

        binding.rootImageView.load(linkToImage) {
            crossfade(true)
            placeholder(R.drawable.ic_broken_image)
        }

        binding.openYandexButton.setOnClickListener {
            viewModel.findImage(linkToImage)
        }

        val endLink = java.net.URLEncoder.encode(linkToImage, "utf-8")

        Log.d("endLink",endLink)

        val images = GlobalScope.async {
            val doc: Document = Jsoup.connect("https://yandex.ru/images/search?rpt=imageview&url=$endLink").get()

            Log.d("doc", doc.toString())

            val elements : Elements = doc.select("div.CbirSites-Item")

            Log.d("elements", elements.toString())

            val images : MutableList<FindImageEntity> = mutableListOf()

            elements.forEach {
                Log.d("elements", "found element")
                val thumb: Elements = it.select("div.CbirSites-ItemThumb")
                val link: String = thumb.select("a").attr("href")

                val title_item: Elements = it.select("div.CbirSites-ItemInfo")
                val title_div: Elements = title_item.select("div.CbirSites-ItemTitle")


                val title : String? = TextOperations().cleanComment(title_div.select("a").html())
                if (title!!.isNotEmpty())
                    images.add(FindImageEntity(title,link))
            }
            images
        }

        GlobalScope.launch(Dispatchers.Main) {
            adapter.setData(images.await())
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}