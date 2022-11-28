package com.example.trip.views.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.trip.R
import com.example.trip.activities.MainActivity
import com.example.trip.databinding.FragmentCreateEditDayPlanBinding
import com.example.trip.models.DayPlanIcon
import com.example.trip.models.Resource
import com.example.trip.utils.*
import com.example.trip.viewmodels.dayplan.CreateEditDayPlanViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.skydoves.powermenu.CustomPowerMenu
import com.skydoves.powermenu.OnMenuItemClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import javax.inject.Inject


@AndroidEntryPoint
class CreateEditDayPlanFragment @Inject constructor() :
    BaseFragment<FragmentCreateEditDayPlanBinding>() {

    private val formatter = DateTimeFormatter.ofPattern(DATE_FORMAT)

    private val viewModel: CreateEditDayPlanViewModel by viewModels()

    private val args: CreateEditDayPlanFragmentArgs by navArgs()

    private lateinit var powerMenu: CustomPowerMenu<Int, IconSpinnerAdapter>

    private val iconsList = listOf(
        DayPlanIcon.MONUMENT.resId,
        DayPlanIcon.CASTLE.resId,
        DayPlanIcon.BOAT.resId,
        DayPlanIcon.CITY.resId,
        DayPlanIcon.WALK.resId,
        DayPlanIcon.WATER.resId,
        DayPlanIcon.MOUNTAIN.resId,
        DayPlanIcon.RESTAURANT.resId
    )

    override fun prepareBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentCreateEditDayPlanBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupArgs()
        setMenu()
        setupOnDateTextChangeListener()
        setupOnNameTextChangeListener()
        onLayoutIconClick()
        onSubmitClick()
        onBackArrowClick()
    }

    private fun setMenu() {
        powerMenu = CustomPowerMenu.Builder<Int, IconSpinnerAdapter>(
            requireContext(),
            IconSpinnerAdapter()
        )
            .addItemList(iconsList)
            .setOnMenuItemClickListener(onIconMenuItemClickListener)
            .setAutoDismiss(true)
            .setLifecycleOwner(viewLifecycleOwner)
            .setWidth((requireContext().displaySize().x * 0.15f).toInt())
            .build()
    }

    private fun onBackArrowClick() {
        binding.buttonBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupArgs() {
        with(binding) {
            imageIcon.setImageResource(iconsList[0])
            args.dayPlan?.let {
                viewModel.toPost = false
                textNewDayPlan.text = resources.getString(R.string.text_edit_day_plan)
                editTextName.setText(it.name)
                viewModel.name = it.name
                editTextDate.setText(it.date.format(formatter))
                viewModel.date = it.date
                imageIcon.setImageResource(getIconResource(it.iconCode))
            }
        }
    }

    private fun getIconCode(iconId: Int): Int {
        return when (iconId) {
            DayPlanIcon.BOAT.resId -> DayPlanIcon.BOAT.code
            DayPlanIcon.CASTLE.resId -> DayPlanIcon.CASTLE.code
            DayPlanIcon.CITY.resId -> DayPlanIcon.CITY.code
            DayPlanIcon.WALK.resId -> DayPlanIcon.WALK.code
            DayPlanIcon.WATER.resId -> DayPlanIcon.WATER.code
            DayPlanIcon.MONUMENT.resId -> DayPlanIcon.MONUMENT.code
            DayPlanIcon.MOUNTAIN.resId -> DayPlanIcon.MOUNTAIN.code
            DayPlanIcon.RESTAURANT.resId -> DayPlanIcon.RESTAURANT.code
            else -> 0
        }
    }

    private fun getIconResource(iconCode: Int): Int {
        return when (iconCode) {
            DayPlanIcon.BOAT.code -> DayPlanIcon.BOAT.resId
            DayPlanIcon.CASTLE.code -> DayPlanIcon.CASTLE.resId
            DayPlanIcon.CITY.code -> DayPlanIcon.CITY.resId
            DayPlanIcon.WALK.code -> DayPlanIcon.WALK.resId
            DayPlanIcon.WATER.code -> DayPlanIcon.WATER.resId
            DayPlanIcon.MONUMENT.code -> DayPlanIcon.MONUMENT.resId
            DayPlanIcon.MOUNTAIN.code -> DayPlanIcon.MOUNTAIN.resId
            DayPlanIcon.RESTAURANT.code -> DayPlanIcon.RESTAURANT.resId
            else -> 0
        }
    }

    private fun setupOnNameTextChangeListener() {
        binding.textFieldName.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                viewModel.name = editable?.toString()
                binding.textFieldName.startIconDrawable?.setTint(
                    resources.getColor(
                        R.color.primary,
                        null
                    )
                )
                binding.textFieldName.error = null
            }
        })
    }

    private fun setupOnDateTextChangeListener() {
        binding.editTextDate.setOnClickListener {
            setAndShowCalendar()
        }
    }

    private fun setAndShowCalendar() {
        val calendar =
            MaterialDatePicker.Builder.datePicker()
                .setTheme(R.style.ThemeOverlay_App_DatePicker).build()

        calendar.addOnPositiveButtonClickListener {
            val date = it.toLocalDate()
            viewModel.date = date
            binding.editTextDate.setText(date.format(formatter))
            binding.textFieldDate.error = null
            binding.textFieldDate.startIconDrawable?.setTint(
                resources.getColor(
                    R.color.primary,
                    null
                )
            )
        }
        calendar.show(childFragmentManager, "DatePicker")
    }

    private fun onLayoutIconClick() {
        binding.layoutIcon.setOnClickListener {
            powerMenu.showAsDropDown(it)
        }
    }

    private val onIconMenuItemClickListener =
        OnMenuItemClickListener<Int> { _, item ->
            binding.imageIcon.setImageResource(item)
            viewModel.icon = getIconCode(item)
        }

    private fun onSubmitClick() {
        binding.buttonSubmit.setOnClickListener {
            submit()
        }
    }

    private fun submit() {
        if (isSubmitNotPermitted()) return
        enableLoading()

        val operation =
            if (viewModel.toPost) viewModel.postDayPlanAsync() else viewModel.updateDayPlanAsync()
        lifecycleScope.launch {
            when (operation.await()) {
                is Resource.Success -> {
                    disableLoading()
                    findNavController().popBackStackWithRefresh()
                }
                is Resource.Loading -> {}
                is Resource.Failure -> {
                    disableLoading()
                    (requireActivity() as MainActivity).showSnackbar(
                        requireView(),
                        R.string.text_post_failure,
                        R.string.text_retry
                    ) {
                        submit()
                    }
                }
            }

        }
    }

    private fun isSubmitNotPermitted(): Boolean {
        var showError = false

        with(binding) {
            if (viewModel.name.isNullOrEmpty()) {
                textFieldName.error = getString(R.string.text_text_empty)
                textFieldName.startIconDrawable?.setTint(resources.getColor(R.color.red, null))
                showError = true
            }
            if (viewModel.date == null) {
                textFieldDate.error = getString(R.string.text_text_empty)
                textFieldDate.startIconDrawable?.setTint(resources.getColor(R.color.red, null))
                showError = true
            }
        }

        return showError
    }

    private fun enableLoading() {
        with(binding) {
            textFieldName.isEnabled = false
            textFieldDate.isEnabled = false
            layoutIcon.isEnabled = false
            buttonSubmit.isEnabled = false
            layoutLoading.setVisible()
        }
    }

    private fun disableLoading() {
        with(binding) {
            textFieldName.isEnabled = true
            textFieldDate.isEnabled = true
            layoutIcon.isEnabled = true
            buttonSubmit.isEnabled = true
            layoutLoading.setGone()
        }
    }

    companion object {
        const val DATE_FORMAT = "dd.MM.yyyy"
    }

}