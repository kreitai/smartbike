/*
 * MIT License
 *
 * Copyright (c) 2020 Kreitai OÜ
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.kreitai.orangebikes.map.view

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.kreitai.orangebikes.R
import com.kreitai.orangebikes.core.utils.PermissionUtils
import com.kreitai.orangebikes.core.view.BaseFragment
import com.kreitai.orangebikes.databinding.FragmentStationMapBinding
import com.kreitai.orangebikes.map.ext.toBitmapDescriptor
import com.kreitai.orangebikes.map.presentation.StationItem
import com.kreitai.orangebikes.map.presentation.StationMapVm
import kotlinx.android.synthetic.main.fragment_station_map.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class StationMapFragment :
    BaseFragment<StationMapVm, FragmentStationMapBinding>(),
    OnMapReadyCallback {

    companion object {
        private const val INITIAL_ZOOM = 17.0f
        private const val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"
        private const val LOCATION_PERMISSION_REQUEST_CODE = 999
    }

    private var isUpdatingContinuously: Boolean = false
    private lateinit var locationCallback: LocationCallback
    private var bikeBitmapMany: Bitmap? = null
    private var bikeBitmapLow: Bitmap? = null
    private var bikeBitmapEmpty: Bitmap? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var permissionDenied: Boolean = false
    private var googleMap: GoogleMap? = null

    override val layoutResource: Int
        get() = R.layout.fragment_station_map

    private val stationMapVm by viewModel<StationMapVm> { parametersOf(resources) }

    override fun bindViewModel(
        viewBinding: FragmentStationMapBinding,
        viewModel: StationMapVm
    ) {
        viewBinding.view = this
        viewBinding.vm = viewModel
        viewModel.isWalkingMode.observe(viewLifecycleOwner, Observer {
            if (it) {
                stopContinuousLocationUpdates()
            } else {
                requestLocationUpdate(isContinuous = true)
            }
        })
    }

    private lateinit var mapView: MapView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                val location = locationResult.lastLocation
                val stations = getViewModel().renderedState.value?.stations
                location?.let {
                    getViewModel().findNearestStation(it, stations)
                        ?.let { stationLatLng: LatLng ->
                            if (!isUpdatingContinuously)
                                stopContinuousLocationUpdates()

                            val builder: LatLngBounds.Builder = LatLngBounds.Builder()
                            builder.include(LatLng(location.latitude, location.longitude))
                            builder.include(stationLatLng)

                            val bounds: LatLngBounds = builder.build()

                            val width = resources.displayMetrics.widthPixels
                            val height = resources.displayMetrics.heightPixels
                            val padding = (width * 0.20).toInt()
                            googleMap?.let { map: GoogleMap ->
                                map.moveCamera(
                                    CameraUpdateFactory.newLatLngBounds(
                                        bounds,
                                        width,
                                        height,
                                        padding
                                    )
                                )
                            }
                        }
                }
            }
        }

        fusedLocationClient = LocationServices
            .getFusedLocationProviderClient(requireActivity())

        bikeBitmapMany = getBitmap(R.drawable.ic_bike_round)
        bikeBitmapLow = getBitmap(R.drawable.ic_bike_round_low)
        bikeBitmapEmpty = getBitmap(R.drawable.ic_bike_round_empty)
    }

    private fun stopContinuousLocationUpdates() {
        isUpdatingContinuously = false
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun getBitmap(iconId: Int): Bitmap? {
        val vectorDrawable: Drawable? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                requireContext().getDrawable(iconId)
            } else {
                ContextCompat.getDrawable(requireContext(), iconId)
            }
        if (vectorDrawable != null) {
            val w = vectorDrawable.intrinsicWidth
            val h = vectorDrawable.intrinsicHeight

            vectorDrawable.setBounds(0, 0, w, h)
            val bm =
                Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bm)
            vectorDrawable.draw(canvas)
            return bm
        }
        return null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }
        mapView = map
        mapView.onCreate(mapViewBundle)
        mapView.getMapAsync(this)
        getViewModel().renderedState
            .observe(viewLifecycleOwner, Observer { stationsViewState ->

                stationsViewState.stations?.let { stationItems ->
                    drawMarkers(stationItems)
                }
            })
    }

    private fun drawMarkers(stationItems: List<StationItem>) {
        val bitmapDescriptorMany = bikeBitmapMany.toBitmapDescriptor()
        val bitmapDescriptorLow = bikeBitmapLow.toBitmapDescriptor()
        val bitmapDescriptorEmpty = bikeBitmapEmpty.toBitmapDescriptor()
        for (station in stationItems) {
            val bikeIcon = when (station.state) {
                StationItem.StationState.MANY -> bitmapDescriptorMany
                StationItem.StationState.LOW -> bitmapDescriptorLow
                StationItem.StationState.EMPTY -> bitmapDescriptorEmpty
            }
            if (googleMap?.projection?.visibleRegion?.latLngBounds?.contains(
                    station.latLng
                ) == true
            ) {
                googleMap?.addMarker(
                    MarkerOptions().position(
                        station.latLng
                    ).title(station.name).icon(bikeIcon)
                        .snippet(station.availableBikes)
                )
            }
        }
    }

    override fun getViewModel(): StationMapVm {
        return stationMapVm
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        this.googleMap = googleMap
        googleMap?.setOnCameraIdleListener {
            getViewModel().renderedState.value?.stations?.let { stationItems ->
                drawMarkers(stationItems)
            }
        }
        enableMyLocation()
        getViewModel().fetchStations()
    }

    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap?.isMyLocationEnabled = true
            val locationResult: Task<Location?> = fusedLocationClient.lastLocation
            requestLocationUpdate(isContinuous = false)
            locationResult.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Set the map's camera position to the current location of the device.
                    val location = task.result
                    val currentLatLng = LatLng(
                        location?.latitude ?: 25.033,
                        location?.longitude ?: 121.52
                    )
                    googleMap?.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            currentLatLng,
                            INITIAL_ZOOM
                        )
                    )
                }
            }

        } else {
            // Permission to access the location is missing. Show rationale and request permission
            PermissionUtils.requestPermission(
                requireActivity() as AppCompatActivity?, LOCATION_PERMISSION_REQUEST_CODE,
                Manifest.permission.ACCESS_FINE_LOCATION, true
            )
            val taipei = LatLng(25.033, 121.52)
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(taipei, INITIAL_ZOOM))
        }
    }

    private fun requestLocationUpdate(isContinuous: Boolean) {
        isUpdatingContinuously = isContinuous
        val request: LocationRequest = LocationRequest.create()
            .setInterval(1000)
            .setFastestInterval(16) // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        var mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle)
        }

        mapView.onSaveInstanceState(mapViewBundle)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError()
            permissionDenied = false
        }
    }

    private fun showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
            .newInstance(true).show(parentFragmentManager, "dialog")
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return
        }
        if (PermissionUtils.isPermissionGranted(
                permissions,
                grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation()
        } else {
            // Permission was denied. Display an error message
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true
        }
    }

}
