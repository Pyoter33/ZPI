package com.example.trip.views.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trip.Constants
import com.example.trip.R
import com.example.trip.activities.HomeActivity
import com.example.trip.activities.LoginActivity
import com.example.trip.activities.MainActivity
import com.example.trip.adapters.GroupsClickListener
import com.example.trip.adapters.GroupsListAdapter
import com.example.trip.databinding.FragmentGroupsListBinding
import com.example.trip.models.Group
import com.example.trip.models.GroupStatus
import com.example.trip.models.Resource
import com.example.trip.utils.*
import com.example.trip.viewmodels.groups.GroupsListViewModel
import com.example.trip.views.dialogs.DeleteGroupDialog
import com.example.trip.views.dialogs.DeleteGroupDialogClickListener
import com.example.trip.views.dialogs.MenuPopupFactory
import com.skydoves.balloon.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class GroupsListFragment @Inject constructor() : BaseFragment<FragmentGroupsListBinding>(),
    GroupsClickListener, DeleteGroupDialogClickListener {

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
                Handler(Looper.getMainLooper()).postDelayed(
                    { doubleBackToExitPressedOnce = false },
                    2000
                )
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
        refreshIfNewData { viewModel.refreshData() }
        startGroup()
    }

    private fun startGroup() {
        requireActivity().intent.extras?.getParcelable<Group>(Constants.GROUP_KEY)?.let {
            onClick(it)
        }
    }

    private fun onCreateClick() {
        binding.buttonAdd.setOnClickListener {
            findNavController().navigate(GroupsListFragmentDirections.actionGroupsListFragmentToCreateEditGroupFragment())
        }
    }

    private fun onSettingsClick() {
        binding.buttonSettings.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), it)
            popupMenu.menuInflater.inflate(R.menu.user_options_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.edit_user_data -> {
                        findNavController().navigate(GroupsListFragmentDirections.actionGroupsListFragmentToEditUserFragment())
                        true
                    }
                    R.id.sign_out -> {
                        signOut()
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    private fun signOut() {
        val preferences =
            requireContext().getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE)
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
                    if (it.data.isEmpty()) binding.textEmptyList.setVisible() else binding.textEmptyList.setGone()
                    adapter.submitList(it.data)
                    binding.layoutRefresh.isRefreshing = false
                }
                is Resource.Loading -> {
                    binding.layoutRefresh.isRefreshing = true
                }
                is Resource.Failure -> {
                    (requireActivity() as HomeActivity).showSnackbar(
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
        when {
            group.groupStatus == GroupStatus.PLANNING -> {
                activityIntent.putExtra(Constants.STATUS_KEY, GroupStatus.PLANNING.code)
                activityIntent.putExtra(Constants.START_CITY, group.startCity)
                activityIntent.putExtra(
                    Constants.COORDINATORS_KEY,
                    group.coordinators.toLongArray()
                )
            }
            group.groupStatus == GroupStatus.ONGOING && group.endDate?.isAfter(LocalDate.now()) == false -> {
                activityIntent.putExtra(Constants.STATUS_KEY, GroupStatus.ONGOING.code)
                activityIntent.putExtra(
                    Constants.COORDINATORS_KEY,
                    group.coordinators.toLongArray()
                )
            }
            else -> {
                activityIntent.putExtra(Constants.STATUS_KEY, GroupStatus.FINISHED.code)
                activityIntent.putExtra(
                    Constants.COORDINATORS_KEY,
                    group.coordinators.toLongArray()
                )
            }
        }
        activityIntent.putExtra(Constants.CURRENCY_KEY, group.currency)
        activityIntent.putExtra(Constants.GROUP_ID_KEY, group.id)
        startActivity(activityIntent)
        requireActivity().finish()
    }

    override fun onLongClick(group: Group, view: View) {
        if (isCoordinator(group.coordinators)) {
            popupMenu.setOnPopupButtonClick(R.id.button_edit) {
                if (group.groupStatus == GroupStatus.ONGOING) {
                    requireContext().toast(R.string.text_cannot_edit_group)
                } else {
                    onMenuEditClick(group)
                }
            }
            popupMenu.setOnPopupButtonClick(R.id.button_delete) {
                onMenuDeleteClick(group)
            }
            popupMenu.showAlignBottom(view)
        }
    }

    private fun isCoordinator(coordinators: List<Long>) =
        preferencesHelper.getUserId() in coordinators

    private fun onMenuEditClick(group: Group) {
        findNavController().navigate(
            GroupsListFragmentDirections.actionGroupsListFragmentToCreateEditGroupFragment(
                group
            )
        )
    }

    private fun onMenuDeleteClick(group: Group) {
        val deleteDialog = DeleteGroupDialog(this, group)
        deleteDialog.show(childFragmentManager, DeleteGroupDialog.TAG)
    }

    override fun onDeleteClick(group: Group) {
        binding.layoutRefresh.isRefreshing = true
        lifecycleScope.launch {
            when (viewModel.deleteGroupAsync(group.id).await()) {
                is Resource.Success -> {
                    viewModel.refreshData()
                }
                is Resource.Failure -> {
                    binding.layoutRefresh.isRefreshing = false
                    (requireActivity() as MainActivity).showSnackbar(
                        requireView(),
                        R.string.text_delete_failure,
                        R.string.text_retry
                    ) {
                        onMenuDeleteClick(group)
                    }
                }
                else -> {
                    //NO-OP
                }
            }
        }
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
}