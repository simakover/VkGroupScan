package ru.sedavnyh.vkgroupscan.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.sedavnyh.vkgroupscan.R
import ru.sedavnyh.vkgroupscan.data.database.VkDao
import ru.sedavnyh.vkgroupscan.data.network.ApiModule
import ru.sedavnyh.vkgroupscan.databinding.FragmentPostsBinding
import ru.sedavnyh.vkgroupscan.di.Scopes
import ru.sedavnyh.vkgroupscan.models.wallGetModel.Post
import ru.sedavnyh.vkgroupscan.util.Constants
import toothpick.Toothpick
import javax.inject.Inject

class PostsFragment : Fragment() {

    private var _binding: FragmentPostsBinding? = null
    private val binding get() =_binding!!

    private var compDisp = CompositeDisposable()

    @Inject
    lateinit var vkDao : VkDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostsBinding.inflate(inflater, container, false)

        Toothpick.inject(this, Toothpick.openScope(Scopes.APP_SCOPE))

        val response = ApiModule().getApiService().wallGet(Constants.ACCESS_TOKEN, Constants.API_VERSION,"-140579116")

        binding.selectButton.setOnClickListener {
            selectFromDb()
        }

        binding.insertButton.setOnClickListener {
            compDisp.add(
                response
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    //.doOnSubscribe { viewState.showProgressBar(true) }
                    .subscribe({
                        //viewState.showProgressBar(false)
                        if (it == null) {
                            Log.d("response", "No found")
                        } else {
                            Log.d("response", "count = ${it.response?.count}")
                            it.response?.posts?.map { post ->
                                Log.d("inserting", "${post.id}")
                                insertIntoDb(post)
                            }
                        }
                    }, {
                        Log.d("response", it.toString())
                    })
            )
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun insertIntoDb(post: Post) {
        Observable.fromCallable {
            Runnable {
                vkDao.insertPost(post)
            }.run()
        }
            .subscribeOn(Schedulers.io())
    }

    fun selectFromDb() {
        vkDao.selectPosts()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({
                Log.d("txt_click", "${it.size}")
            }, {
                Log.d("throwable", it.toString())
            })
    }

}