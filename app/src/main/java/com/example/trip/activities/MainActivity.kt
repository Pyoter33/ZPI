package com.example.trip.activities

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.trip.Constants
import com.example.trip.R
import com.example.trip.models.GroupStatus
import com.example.trip.utils.setAppLocale
import com.example.trip.utils.setGone
import com.example.trip.utils.setVisible
import com.example.trip.views.dialogs.LeaveGroupDialog
import com.example.trip.views.dialogs.LeaveGroupDialogClickListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), LeaveGroupDialogClickListener {

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
            R.id.moneyPager,
            R.id.participantsFragmentTrip,
            R.id.summaryFragment,
            R.id.tripSummary
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setNavigation()
        setSettings()
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

    private fun setSettings() {
        val buttonSettings = findViewById<ImageButton>(R.id.button_settings)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in rootDestinations) {
                buttonSettings.setVisible()
            } else {
                buttonSettings.setGone()
            }
        }

        buttonSettings.setOnClickListener {
            val powerMenu = PowerMenu.Builder(this)
                .addItem(PowerMenuItem("Leave group"))
                .setAutoDismiss(true)
                .setShowBackground(false)
                .setTextTypeface(Typeface.create("roboto_medium", Typeface.NORMAL))
                .setTextSize(13)
                .setMenuColor(Color.WHITE)
                .setOnMenuItemClickListener { position, _ ->
                    when(position) {
                        0 -> showDialog()
                    }
                }
                .build()
            powerMenu.showAsAnchorLeftBottom(it,0, 10)
        }
    }

    private fun showDialog() {
        val leaveDialog = LeaveGroupDialog(this)
        leaveDialog.show(supportFragmentManager, LeaveGroupDialog.TAG)
    }

    private fun setNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        navController = navHostFragment.navController
        bottomNavigationView = findViewById(R.id.bottom_navigation_view)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.accommodation -> navController.navigate(R.id.accommodation, intent.extras)
                R.id.availability -> navController.navigate(R.id.availability, intent.extras)
                R.id.participants -> navController.navigate(R.id.participants, intent.extras)
                R.id.dayPlan -> navController.navigate(R.id.dayPlan, intent.extras)
                R.id.finances -> navController.navigate(R.id.finances, intent.extras)
                R.id.participantsFragmentTrip -> navController.navigate(
                    R.id.participantsFragmentTrip,
                    intent.extras
                )
                R.id.summary -> navController.navigate(R.id.summary, intent.extras)
                R.id.tripSummary -> navController.navigate(R.id.tripSummary, intent.extras)
            }
            true
        }

        val navGraph = when (intent.extras!!.getInt(Constants.STATUS_KEY)) {
            GroupStatus.PLANNING.code -> {
                bottomNavigationView.menu.clear()
                bottomNavigationView.inflateMenu(R.menu.navigation_pre_trip_menu)
                navController.navInflater.inflate(R.navigation.pre_trip_graph)
            }
            GroupStatus.ONGOING.code -> {
                bottomNavigationView.menu.clear()
                bottomNavigationView.inflateMenu(R.menu.navigation_trip_menu)
                navController.navInflater.inflate(R.navigation.trip_graph)
            }
            else -> navController.navInflater.inflate(R.navigation.post_trip_graph)
        }
        navController.setGraph(navGraph, intent.extras)
    }

    fun showSnackbar(
        view: View,
        @StringRes messageResId: Int,
        @StringRes actionResId: Int,
        length: Int = Snackbar.LENGTH_LONG,
        action: () -> Unit
    ) {
        Snackbar.make(view, messageResId, length)
            .setAction(actionResId) {
                action()
            }
            .setAnchorView(bottomNavigationView)
            .setBackgroundTint(resources.getColor(R.color.grey400, null))
            .setTextColor(resources.getColor(R.color.black, null))
            .setActionTextColor(resources.getColor(R.color.primary, null))
            .show()
    }

    override fun onLeaveClick() {

    }

}