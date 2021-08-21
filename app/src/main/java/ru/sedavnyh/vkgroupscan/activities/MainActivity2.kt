package ru.sedavnyh.vkgroupscan.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.sedavnyh.vkgroupscan.R
import ru.sedavnyh.vkgroupscan.di.Scopes
import ru.sedavnyh.vkgroupscan.di.contextModule
import toothpick.Toothpick

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        Toothpick
            .openScope(Scopes.APP_SCOPE)
            .installModules(
                contextModule(this)
            )
    }
}