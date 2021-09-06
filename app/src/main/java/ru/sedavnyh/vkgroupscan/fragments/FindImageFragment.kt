package ru.sedavnyh.vkgroupscan.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import ru.sedavnyh.vkgroupscan.adapters.FindImageAdapter
import ru.sedavnyh.vkgroupscan.databinding.FragmentFindImageBinding
import ru.sedavnyh.vkgroupscan.models.entities.FindImageEntity
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

        val adapter = FindImageAdapter(viewModel::openTachiyomiWithQuery)
        binding.FindImageRecyclerView.adapter = adapter
        binding.FindImageRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val linkToImage = args.linkToImage

        val images = GlobalScope.async {
            val doc: Document = Jsoup.connect("https://yandex.ru/images/search?rpt=imageview&url=$linkToImage").get()
            val elements : Elements = doc.select("div.CbirSites-Item")
            val images : MutableList<FindImageEntity> = mutableListOf()

            elements.forEach {
                val thumb: Elements = it.select("div.CbirSites-ItemThumb")
                val link: String = thumb.select("a").attr("href")
                val title : String = it.select("a.Link_theme_normal").html()
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