package com.example.trip.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.example.trip.activities.MainActivity

abstract class BaseFragment<T: ViewDataBinding>: Fragment() {

    private var _binding: T? = null
    protected val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = prepareBinding(inflater, container)
        return binding.root
    }

    protected abstract fun prepareBinding(inflater: LayoutInflater, container: ViewGroup?): T

    override fun onDestroyView() {
        super.onDestroyView()
        (requireActivity() as? MainActivity)?.hideSnackbar()
        _binding = null
    }
}