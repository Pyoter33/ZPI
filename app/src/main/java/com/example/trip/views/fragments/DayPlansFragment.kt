package com.example.trip.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.trip.R
import kotlin.properties.Delegates

class DayPlansFragment : Fragment() {

    private var groupId by Delegates.notNull<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_day_plans, container, false)
    }

    companion object {
        private const val PLACEHOLDER_USERID = 1
        private const val GROUP_ID_ARG = "groupId"
    }
}