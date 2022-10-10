package com.example.trip.activities

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.trip.R
import com.example.trip.models.TripStatus
import com.example.trip.utils.setAppLocale
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        private const val APP_LOCALE = "gb"
    }

    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView
    private val rootDestinations =
        listOf(
            R.id.accommodationListFragment,
            R.id.availabilityPager,
            R.id.participantsFragmentPreTrip,
            R.id.dayPlansFragment,
            R.id.financesFragment,
            R.id.participantsFragmentTrip
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setNavigation()
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ContextWrapper(newBase.setAppLocale(APP_LOCALE)))
    }

    override fun onBackPressed() {
        if (navController.currentDestination?.id in rootDestinations) {
            val activityIntent = Intent(this, HomeActivity::class.java)
            startActivity(activityIntent)
            finish()
        } else {
            super.onBackPressed()
        }
    }

    private fun setNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        navController = navHostFragment.navController
        bottomNavigationView = findViewById(R.id.bottom_navigation_view)
        setupWithNavController(bottomNavigationView, navController)

        val navGraph = when (intent.extras!!.getInt("status")) {
            TripStatus.PRE_TRIP.code -> {
                bottomNavigationView.menu.clear()
                bottomNavigationView.inflateMenu(R.menu.navigation_pre_trip_menu)
                navController.navInflater.inflate(R.navigation.pre_trip_graph)
            }
            TripStatus.TRIP.code -> {
                bottomNavigationView.menu.clear()
                bottomNavigationView.inflateMenu(R.menu.navigation_trip_menu)
                navController.navInflater.inflate(R.navigation.trip_graph)
            }
            else -> navController.navInflater.inflate(R.navigation.post_trip_graph) //
        }

        navController.graph = navGraph
    }

}