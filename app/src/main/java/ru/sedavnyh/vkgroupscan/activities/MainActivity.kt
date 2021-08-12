package ru.sedavnyh.vkgroupscan.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.terrakok.cicerone.Navigator
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.androidx.AppNavigator
import ru.sedavnyh.vkgroupscan.R
import ru.sedavnyh.vkgroupscan.di.Scopes.APP_SCOPE
import ru.sedavnyh.vkgroupscan.navigation.Screens
import toothpick.Toothpick
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var router : Router

    @Inject
    lateinit var navigatorHolder : NavigatorHolder

    private val navigator: Navigator = AppNavigator(this, R.id.flow_container)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Toothpick.inject(this, Toothpick.openScope(APP_SCOPE))
        navigatorHolder.setNavigator(navigator)
        router.newRootScreen(Screens.postsScreen())
    }
}