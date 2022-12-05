package com.example.trip.activities

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.example.trip.Constants
import com.example.trip.R
import com.example.trip.models.GroupStatus
import com.example.trip.models.Resource
import com.example.trip.utils.setAppLocale
import com.example.trip.utils.setGone
import com.example.trip.utils.setVisible
import com.example.trip.viewmodels.MainActivityViewModel
import com.example.trip.views.dialogs.LeaveGroupDialog
import com.example.trip.views.dialogs.LeaveGroupDialogClickListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), LeaveGroupDialogClickListener {

    companion object {
        private const val APP_LOCALE = "gb"
    }

    private val viewModel: MainActivityViewModel by viewModels()

    private var snackbar: Snackbar? = null
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
            val popupMenu = PopupMenu(this, it)
            popupMenu.menuInflater.inflate(R.menu.group_options_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.leave_group -> {
                        showDialog()
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
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

        val options = NavOptions.Builder().build()

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.accommodation -> navController.navigate(R.id.accommodation, intent.extras, options)
                R.id.availability -> navController.navigate(R.id.availability, intent.extras, options)
                R.id.participants -> navController.navigate(R.id.participants, intent.extras, options)
                R.id.dayPlan -> navController.navigate(R.id.dayPlan, intent.extras, options)
                R.id.finances -> navController.navigate(R.id.finances, intent.extras, options)
                R.id.participantsFragmentTrip -> navController.navigate(R.id.participantsFragmentTrip, intent.extras, options)
                R.id.summary -> navController.navigate(R.id.summary, intent.extras, options)
                R.id.tripSummary -> navController.navigate(R.id.tripSummary, intent.extras, options)
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
            else -> {
                bottomNavigationView.menu.clear()
                bottomNavigationView.inflateMenu(R.menu.navigation_post_trip_menu)
                bottomNavigationView.menu.findItem(R.id.finances).isChecked = true
                navController.navInflater.inflate(R.navigation.post_trip_graph)
            }
        }
        navController.setGraph(navGraph, intent.extras)
    }

    fun showSnackbar(
        view: View,
        @StringRes messageResId: Int,
        @StringRes actionResId: Int,
        length: Int = Snackbar.LENGTH_LONG,
        anchor: View = bottomNavigationView,
        action: () -> Unit
    ) {
        snackbar = Snackbar.make(view, messageResId, length)
            .setAction(actionResId) {
                action()
            }
            .setAnchorView(anchor)
            .setBackgroundTint(resources.getColor(R.color.grey400, null))
            .setTextColor(resources.getColor(R.color.black, null))
            .setActionTextColor(resources.getColor(R.color.primary, null))
        snackbar!!.show()
    }

    fun showSnackbar(
        view: View,
        message: String,
        @StringRes actionResId: Int,
        length: Int = Snackbar.LENGTH_LONG,
        anchor: View = bottomNavigationView,
        action: () -> Unit
    ) {
        snackbar = Snackbar.make(view, message, length)
            .setAction(actionResId) {
                action()
            }
            .setAnchorView(anchor)
            .setBackgroundTint(resources.getColor(R.color.grey400, null))
            .setTextColor(resources.getColor(R.color.black, null))
            .setActionTextColor(resources.getColor(R.color.primary, null))
        snackbar!!.show()
    }

    fun hideSnackbar() {
        snackbar?.dismiss()
    }

    override fun onLeaveClick() {
        val layoutLoading = findViewById<FrameLayout>(R.id.layout_loading)
        layoutLoading.setVisible()
        lifecycleScope.launch {
            when (val result = viewModel.leaveGroupAsync().await()) {
                is Resource.Success -> {
                    layoutLoading.setGone()
                    val activityIntent = Intent(this@MainActivity, HomeActivity::class.java)
                    startActivity(activityIntent)
                    finish()
                }
                is Resource.Failure -> {
                    layoutLoading.setGone()
                    result.message?.let {
                        showSnackbar(
                            window.decorView.rootView,
                            it,
                            R.string.text_retry
                        ) {
                            onLeaveClick()
                        }
                    } ?: showSnackbar(
                        window.decorView.rootView,
                        R.string.text_leave_failure,
                        R.string.text_retry
                    ) {
                        onLeaveClick()
                    }
                }
                else -> {
                    //NO-OP
                }
            }

        }

    }

}