package com.example.trip.views.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trip.Constants
import com.example.trip.R
import com.example.trip.activities.LoginActivity
import com.example.trip.activities.MainActivity
import com.example.trip.adapters.GroupsClickListener
import com.example.trip.adapters.GroupsListAdapter
import com.example.trip.databinding.FragmentGroupsListBinding
import com.example.trip.models.Group
import com.example.trip.models.GroupStatus
import com.example.trip.models.Resource
import com.example.trip.utils.SharedPreferencesHelper
import com.example.trip.utils.setOnPopupButtonClick
import com.example.trip.utils.setSwipeRefreshLayout
import com.example.trip.utils.toast
import com.example.trip.viewmodels.groups.GroupsListViewModel
import com.example.trip.views.dialogs.MenuPopupFactory
import com.skydoves.balloon.*
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GroupsListFragment @Inject constructor() : BaseFragment<FragmentGroupsListBinding>(), GroupsClickListener {

    private val popupMenu by balloon<MenuPopupFactory>()

    private val viewModel: GroupsListViewModel by viewModels()

    @Inject
    lateinit var preferencesHelper: SharedPreferencesHelper

    private var doubleBackToExitPressedOnce = false

    @Inject
    lateinit var adapter: GroupsListAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    requireActivity().finishAffinity()
                }
                doubleBackToExitPressedOnce = true
                Handler(Looper.getMainLooper()).postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
                requireContext().toast(R.string.text_click_twice)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun prepareBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentGroupsListBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAdapter()
        onCreateClick()
        onSettingsClick()
        observeGroupsList()
        setSwipeRefreshLayout(binding.layoutRefresh, R.color.primary) { viewModel.refreshData() }
    }

    private fun onCreateClick() {
        binding.buttonAdd.setOnClickListener {
            findNavController().navigate(GroupsListFragmentDirections.actionGroupsListFragmentToCreateEditGroupFragment())
        }
    }

    private fun onSettingsClick() {
        binding.buttonSettings.setOnClickListener {
            val powerMenu = PowerMenu.Builder(requireContext())
                .addItem(PowerMenuItem("Edit personal data"))
                .addItem(PowerMenuItem("Sign out"))
                .setAutoDismiss(true)
                .setShowBackground(false)
                .setTextSize(13)
                .setTextTypeface(Typeface.create("roboto_medium", Typeface.NORMAL))
                .setMenuColor(Color.WHITE)
                .setOnMenuItemClickListener { position, _ ->
                    when(position) {
                        0 -> {  }
                        1 -> { signOut() }
                    }
                }
                .build()
            powerMenu.showAsAnchorLeftBottom(it,0, 15)
        }
    }

    private fun signOut() {
        val preferences = requireContext().getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE)
        preferences.edit().remove(Constants.AUTHORIZATION_HEADER).apply()
        preferences.edit().remove(Constants.USER_ID_KEY).apply()

        val activityIntent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(activityIntent)
        requireActivity().finish()
    }


    private fun observeGroupsList() {
        viewModel.groupsList.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    adapter.submitList(it.data)
                    binding.layoutRefresh.isRefreshing = false
                }
                is Resource.Loading -> {
                    binding.layoutRefresh.isRefreshing = true
                }
                is Resource.Failure -> {
                    (requireActivity() as MainActivity).showSnackbar(
                        requireView(),
                        R.string.text_fetch_failure,
                        R.string.text_retry
                    ) {
                        viewModel.refreshData()
                    }
                    binding.layoutRefresh.isRefreshing = false
                }
            }
        }
    }

    private fun setAdapter() {
        adapter.setGroupsClickListener(this)
        binding.attractionsList.adapter = adapter
        binding.attractionsList.layoutManager = LinearLayoutManager(context)
    }

    //list item
    override fun onClick(group: Group) {
        val activityIntent = Intent(requireContext(), MainActivity::class.java)
        when (group.groupStatus) {
            GroupStatus.PLANNING -> {
                activityIntent.putExtra(Constants.STATUS_KEY, GroupStatus.PLANNING.code)
                activityIntent.putExtra(Constants.CURRENCY_KEY, group.currency)
                activityIntent.putExtra(Constants.START_CITY, group.startCity)
                activityIntent.putExtra(Constants.COORDINATORS_KEY, group.coordinators.toLongArray())
            }
            GroupStatus.ONGOING -> {
                activityIntent.putExtra(Constants.STATUS_KEY, GroupStatus.ONGOING.code)
                activityIntent.putExtra(Constants.CURRENCY_KEY, group.currency)
                activityIntent.putExtra(Constants.COORDINATORS_KEY, group.coordinators.toLongArray())
            }
            else -> {
                return
            }

        }
        activityIntent.putExtra(Constants.GROUP_ID_KEY, group.id)
        startActivity(activityIntent)
        requireActivity().finish()
    }

    override fun onLongClick(group: Group, view: View) {
        if(isCoordinator(group.coordinators)) {
            popupMenu.setOnPopupButtonClick(R.id.button_edit) {
                onMenuEditClick(group)
            }
            popupMenu.setOnPopupButtonClick(R.id.button_delete) {
                onMenuDeleteClick(group)
            }
            popupMenu.showAlignBottom(view)
        }
    }

    private fun isCoordinator(coordinators: List<Long>) = preferencesHelper.getUserId() in coordinators

    private fun onMenuEditClick(group: Group) {
        findNavController().navigate(
            GroupsListFragmentDirections.actionGroupsListFragmentToCreateEditGroupFragment(
                group
            )
        )
    }

    private fun onMenuDeleteClick(group: Group) {
        //check if finances are completed
    }

    override fun onInfoClick(group: Group, view: View) {
        val popupMenu = createInfoPopup(group)
        popupMenu.showAlignBottom(view)
    }

    private fun createInfoPopup(group: Group): Balloon {
        return Balloon.Builder(requireContext())
            .setWidthRatio(0.9f)
            .setHeight(BalloonSizeSpec.WRAP)
            .setArrowSize(20)
            .setCornerRadius(20f)
            .setIconSpace(0)
            .setArrowElevation(3)
            .setArrowColorResource(R.color.grey100)
            .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
            .setElevation(3)
            .setArrowOrientation(ArrowOrientation.TOP)
            .setText(group.description!!)
            .setTextGravity(Gravity.START)
            .setPaddingResource(R.dimen.marginM)
            .setMarginHorizontalResource(R.dimen.marginL)
            .setBackgroundColorResource(R.color.grey100)
            .setTextColorResource(R.color.grey900)
            .setTextSizeResource(R.dimen.textSmall)
            .setLifecycleOwner(viewLifecycleOwner)
            .setBalloonAnimation(BalloonAnimation.FADE)
            .build()
    }

    companion object {
        private const val PLACEHOLDER_USERID = 1
    }
}