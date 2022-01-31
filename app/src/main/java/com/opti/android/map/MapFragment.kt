package com.opti.android.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.navigation.NavigationView

class MapFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener,
    OnMapReadyCallback, LocationListener {
    var nLastLocation: Location? = null
    private var mMap: GoogleMap? = null

    @SuppressLint("MissingPermission", "ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_map, container, false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.frg) as SupportMapFragment?
        val myLocationButton = mapFragment!!.requireView().findViewById<View>(0x2)
        val rlp = myLocationButton.layoutParams as (RelativeLayout.LayoutParams)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
        rlp.setMargins(0, 0, 30, 30);

        mapFragment.getMapAsync(this)
        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (hasLocationPermission) {
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                MIN_TIME,
                MIN_DISTANCE,
                this
            )
        }

        return rootView
    }

    private val hasLocationPermission: Boolean
        get() = if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {
            requestPermissions()
            false
        }

    private fun requestPermissions() {
        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            MY_PERMISSIONS_FINE_LOCATION
        )
    }

    override fun onResume() {
        super.onResume()
        if (hasLocationPermission) {
            lastLocation
        }
    }

    @get:SuppressLint("MissingPermission")
    private val lastLocation: Unit
        get() {
            val mFusedLocationClient: FusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(requireActivity())
            mFusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    nLastLocation = location
                    mMap?.isMyLocationEnabled = true
                    updateMapLocation()
                }
                .addOnFailureListener { e ->
                    Log.d(TAG, "Error trying to get last location")
                    e.printStackTrace()
                }
        }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        TODO("Not yet implemented")
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
    }

    override fun onLocationChanged(location: Location) {
        updateMapLocation()
    }

    private fun updateMapLocation() {
        if (nLastLocation != null) {
            val latLng = LatLng(nLastLocation!!.latitude, nLastLocation!!.longitude)
            mMap?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    latLng,
                    ZOOM_LEVEL
                )
            )
        }
    }

    companion object {
        const val MY_PERMISSIONS_FINE_LOCATION = 101
        const val MIN_TIME: Long = 400
        const val MIN_DISTANCE = 1000f
        const val ZOOM_LEVEL = 17f
        const val TAG = "NavMapsActivity"
    }

}