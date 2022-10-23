package com.example.trip.views.fragments

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trip.R
import com.example.trip.activities.MainActivity
import com.example.trip.adapters.GroupsClickListener
import com.example.trip.adapters.GroupsListAdapter
import com.example.trip.databinding.FragmentGroupsListBinding
import com.example.trip.models.Group
import com.example.trip.models.GroupStatus
import com.example.trip.models.Resource
import com.example.trip.utils.setSwipeRefreshLayout
import com.example.trip.utils.toast
import com.example.trip.viewmodels.GroupsListViewModel
import com.skydoves.balloon.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GroupsListFragment @Inject constructor() : Fragment(), GroupsClickListener {

    private lateinit var binding: FragmentGroupsListBinding

    private val viewModel: GroupsListViewModel by viewModels()

    @Inject
    lateinit var adapter: GroupsListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupsListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAdapter()
        onCreateClick()
        observeAccommodationsList()
        setSwipeRefreshLayout(binding.layoutRefresh, R.color.primary) { viewModel.refreshData() }
    }

    private fun onCreateClick() {
        binding.buttonAdd.setOnClickListener {
            findNavController().navigate(GroupsListFragmentDirections.actionGroupsListFragmentToCreateEditGroupFragment())
        }
    }

    private fun observeAccommodationsList() {
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
                    requireContext().toast(R.string.text_fetch_failure)
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
                activityIntent.putExtra("status", GroupStatus.PLANNING.code)
            }
            GroupStatus.ONGOING -> {
                activityIntent.putExtra("status", GroupStatus.ONGOING.code)
            }
            else -> {
                return
            }

        }
        activityIntent.putExtra("groupId", group.id)
        startActivity(activityIntent)
        requireActivity().finish()
    }

    override fun onInfoClick(group: Group, view: View) {
        val popupMenu = createPopup(group)

        popupMenu.showAlignBottom(view)
    }

    private fun createPopup(group: Group): Balloon {
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