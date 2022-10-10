package com.example.trip.activities

import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.trip.R
import com.example.trip.utils.setAppLocale
import com.example.trip.utils.setGone
import com.example.trip.utils.setVisible
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        private const val APP_LOCALE = "gb"
    }

    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView
    private var previousDestinationParent: NavGraph? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setBottomBarNavigation()
        setOnDestinationChangedListener()
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ContextWrapper(newBase.setAppLocale(APP_LOCALE)))
    }

    private fun setBottomBarNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        navController = navHostFragment.navController
        bottomNavigationView = findViewById(R.id.bottom_navigation_view)
        navController.
        setupWithNavController(bottomNavigationView, navController)
        bottomNavigationView.setOnItemSelectedListener { item ->
            navController.navigate(item.itemId, Bundle())
            true
        }
    }

    private fun setOnDestinationChangedListener() {
        navController.addOnDestinationChangedListener { _, destination, arguments ->
            when (destination.parent?.id) {
                R.id.pre_trip -> {
                    changeBottomNavigationMenuIfDifferentParent(
                        R.menu.navigation_pre_trip_menu,
                        destination.parent
                    )
                }
                R.id.trip -> {
                    changeBottomNavigationMenuIfDifferentParent(
                        R.menu.navigation_trip_menu,
                        destination.parent
                    )
                }
                else -> {
                    bottomNavigationView.setGone()
                    previousDestinationParent = null
                }
            }
        }
    }

    private fun changeBottomNavigationMenuIfDifferentParent(
        @MenuRes menuId: Int,
        destinationParent: NavGraph?
    ) {
        if (previousDestinationParent?.id != destinationParent?.id) {
            bottomNavigationView.menu.clear()
            bottomNavigationView.inflateMenu(menuId)
            previousDestinationParent = destinationParent
            bottomNavigationView.setVisible()
        }
    }
}