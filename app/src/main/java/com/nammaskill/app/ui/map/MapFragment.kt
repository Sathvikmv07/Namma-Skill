package com.nammaskill.app.ui.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.chip.Chip
import com.nammaskill.app.R
import com.nammaskill.app.data.model.SkillCenter
import com.nammaskill.app.databinding.FragmentMapBinding

class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MapViewModel by viewModels()
    private var googleMap: GoogleMap? = null
    private var selectedCenter: SkillCenter? = null

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) enableMyLocation()
    }

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _binding = FragmentMapBinding.inflate(inflater, c, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.fabMyLocation.setOnClickListener { requestLocationOrCenter() }
        binding.btnGetDirections.setOnClickListener {
            selectedCenter?.let { openDirections(it) }
        }
        binding.btnViewCourses.setOnClickListener {
            Toast.makeText(requireContext(), "Filtering courses for ${selectedCenter?.district}", Toast.LENGTH_SHORT).show()
        }
        binding.centerInfoCard.visibility = View.GONE

        viewModel.error.observe(viewLifecycleOwner) { err ->
            if (!err.isNullOrEmpty()) Toast.makeText(requireContext(), err, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Center on Karnataka
        val karnataka = LatLng(15.3173, 75.7139)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(karnataka, 6.5f))

        map.setOnMarkerClickListener { marker ->
            val center = marker.tag as? SkillCenter
            center?.let { showCenterInfo(it) }
            true
        }

        map.setOnMapClickListener {
            binding.centerInfoCard.visibility = View.GONE
        }

        viewModel.centers.observe(viewLifecycleOwner) { centers ->
            centers.forEach { center ->
                val pos = LatLng(center.lat, center.lng)
                val marker = map.addMarker(
                    MarkerOptions()
                        .position(pos)
                        .title(center.name)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                )
                marker?.tag = center
            }
        }

        requestLocationOrCenter()
    }

    private fun showCenterInfo(center: SkillCenter) {
        selectedCenter = center
        binding.tvCenterInfoName.text = center.name
        binding.tvCenterInfoAddress.text = center.address
        binding.chipGroupTrades.removeAllViews()
        center.tradesOffered.forEach { trade ->
            val chip = Chip(requireContext()).apply {
                text = trade
                isClickable = false
            }
            binding.chipGroupTrades.addView(chip)
        }
        binding.centerInfoCard.visibility = View.VISIBLE
        binding.centerInfoCard.animate().alpha(1f).translationYBy(-20f).setDuration(300).start()
    }

    private fun requestLocationOrCenter() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED -> enableMyLocation()
            else -> locationPermissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    private fun enableMyLocation() {
        try {
            googleMap?.isMyLocationEnabled = true
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun openDirections(center: SkillCenter) {
        val uri = Uri.parse("https://maps.google.com/maps?daddr=${center.lat},${center.lng}")
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
