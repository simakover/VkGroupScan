package ru.sedavnyh.vkgroupscan.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.sedavnyh.vkgroupscan.R
import ru.sedavnyh.vkgroupscan.data.network.ApiModule
import ru.sedavnyh.vkgroupscan.util.Constants.ACCESS_TOKEN
import ru.sedavnyh.vkgroupscan.util.Constants.API_VERSION

class MainActivity : AppCompatActivity() {

    private var compDisp = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val response = ApiModule().getApiService().wallGet(ACCESS_TOKEN,API_VERSION,"-140579116")

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
                    }
                }, {
                    Log.d("response", it.toString())
                })
        )

    }
}