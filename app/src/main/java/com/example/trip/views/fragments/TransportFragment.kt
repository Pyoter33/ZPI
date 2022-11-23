package com.example.trip.views.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trip.R
import com.example.trip.activities.MainActivity
import com.example.trip.adapters.FlightsPagerAdapter
import com.example.trip.adapters.UserTransportClickListener
import com.example.trip.adapters.UserTransportsAdapter
import com.example.trip.databinding.FragmentTransportBinding
import com.example.trip.models.AirTransport
import com.example.trip.models.CarTransport
import com.example.trip.models.Resource
import com.example.trip.models.UserTransport
import com.example.trip.utils.*
import com.example.trip.viewmodels.transport.TransportViewModel
import com.example.trip.views.dialogs.MenuPopupFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.skydoves.balloon.balloon
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class TransportFragment @Inject constructor() : BaseFragment<FragmentTransportBinding>(),
    OnMapReadyCallback,
    UserTransportClickListener {

    private val viewModel: TransportViewModel by viewModels()

    private lateinit var googleMap: GoogleMap

    private val popupMenu by balloon<MenuPopupFactory>()

    private val args: TransportFragmentArgs by navArgs()

    @Inject
    lateinit var adapter: UserTransportsAdapter

    @Inject
    lateinit var flightsPagerAdapter: FlightsPagerAdapter

    override fun prepareBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentTransportBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHeader()
        onBackArrowClick(binding.buttonBack)
        onAddClick()
        setAdapter()
        createMap()
    }

    private fun setHeader() {
        with(binding) {
            textStartLocation.text = args.startCity
            textDestination.text = args.destination
        }
    }

    private fun createMap() {
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.mapView) as SupportMapFragmentWrapper

        mapFragment.getMapAsync(this)
        mapFragment.setOnTouchListener {
            binding.scrollView.requestDisallowInterceptTouchEvent(true)
        }
    }

    private fun setAdapter() {
        val layoutManager = LinearLayoutManager(context)
        adapter.setUserTransportClickListener(this)
        adapter.setCurrency(args.currency)
        binding.listTransports.adapter = adapter
        binding.listTransports.layoutManager = layoutManager
        binding.listTransports.addItemDecoration(
            MaterialDividerItemDecoration(
                requireContext(),
                layoutManager.orientation
            ).apply {
                isLastItemDecorated = false
            }
        )
    }

    private fun observeTransport() {
        viewModel.transport.observe(viewLifecycleOwner) { transport ->
            when (transport) {
                is Resource.Success -> {
                    transport.data.carTransport?.let {
                        setCarTransport(it)
                        setOnSeeMoreClickListener(it)
                    } ?: hideCarTransport()
                    transport.data.airTransport?.let {
                        setAirTransport(it)
                    } ?: hideAirTransport()

                    adapter.submitList(transport.data.userTransport)
                    binding.layoutLoading.setGone()
                }
                is Resource.Loading -> {
                    binding.layoutLoading.setVisible()
                }
                is Resource.Failure -> {
                    (requireActivity() as MainActivity).showSnackbar(
                        requireView(),
                        R.string.text_fetch_failure,
                        R.string.text_retry,
                        Snackbar.LENGTH_INDEFINITE
                    ) {
                        viewModel.refreshData()
                    }
                    binding.layoutLoading.setGone()
                }
            }
        }
    }

    private fun observeRoute() {
        viewModel.route.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    val polylineOptions = it.data
                    polylineOptions.color(resources.getColor(R.color.primary_darker, null))
                    googleMap.addPolyline(it.data)
                }
                else -> {
                    (requireActivity() as MainActivity).showSnackbar(
                        requireView(),
                        R.string.text_fetch_failure,
                        R.string.text_retry,
                        Snackbar.LENGTH_INDEFINITE
                    ) {
                        viewModel.refreshData()
                    }
                }
            }
        }
    }

    private fun setOnSeeMoreClickListener(carTransport: CarTransport) {
        val link =
            "https://www.google.com/maps/dir/?api=1&origin=${carTransport.sourceLatLng}&destination=${carTransport.destinationLatLng}"

        binding.buttonSeeInMaps.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(link)
            )
            startActivity(intent)
        }

    }

    private fun requestRoute(carTransport: CarTransport) {
        val latLngSplit = carTransport.sourceLatLng.split(',')
        val lat = latLngSplit.first()
        val lng = latLngSplit.last()

        val latLng = LatLng(lat.toDouble(), lng.toDouble())
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 7f))
        viewModel.getRoute(carTransport.sourceLatLng, carTransport.destinationLatLng)
    }

    private fun setCarTransport(carTransport: CarTransport) {
        with(binding) {
            textDistance.text = getString(
                R.string.format_km, carTransport.distance.toFloat()
            )
            textDurationCar.text = carTransport.duration.toStringTime()
        }
        requestRoute(carTransport)
    }

    private fun hideCarTransport() {

    }

    private fun setAirTransport(airTransport: AirTransport) {
        with(binding) {
            pagerFlights.adapter = flightsPagerAdapter
            TabLayoutMediator(tabPager, pagerFlights) { _, _ -> }.attach()

            flightsPagerAdapter.submitList(airTransport.flights)
            textTransfers.text = airTransport.flights.size.minus(1).toString()
            textDurationToAirport.text =
                airTransport.flights.first().travelToAirportDuration!!.toStringTime()
            textDurationFlight.text =
                airTransport.duration.minus(airTransport.flights.first().travelToAirportDuration!!)
                    .minus(airTransport.flights.last().travelToAccommodationDuration!!)
                    .toStringTime()
            textDurationFromAirport.text =
                airTransport.flights.last().travelToAccommodationDuration!!.toStringTime()
            textDurationTotal.text = airTransport.duration.toStringTime()

        }
    }

    private fun hideAirTransport() {

    }

    private fun onAddClick() {
        binding.buttonAdd.setOnClickListener {
            findNavController().navigate(
                TransportFragmentDirections.actionTransportFragmentToCreateEditTransportFragment(
                    args.groupId,
                    args.accommodationId,
                    args.currency
                )
            )
        }
    }

    override fun onLongClick(userTransport: UserTransport, view: View) {
        popupMenu.apply {
            setOnPopupButtonClick(R.id.button_edit) { onMenuEditClick(userTransport) }
            setOnPopupButtonClick(R.id.button_delete) { onMenuDeleteClick(userTransport) }
        }
        popupMenu.showAlignBottom(view)
    }


    private fun onMenuEditClick(userTransport: UserTransport) {
        findNavController().navigate(
            TransportFragmentDirections.actionTransportFragmentToCreateEditTransportFragment(
                args.groupId,
                args.accommodationId,
                args.currency,
                userTransport
            )
        )
    }

    private fun onMenuDeleteClick(userTransport: UserTransport) {

    }

    //map
    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        observeTransport()
        observeRoute()
    }

}