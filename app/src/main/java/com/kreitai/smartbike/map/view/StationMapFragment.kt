/*
 * MIT License
 *
 * Copyright (c) 2019 Kreitai OÜ
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

package com.kreitai.smartbike.map.view

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.kreitai.smartbike.core.view.BaseFragment
import com.kreitai.smartbike.databinding.FragmentStationMapBinding
import com.kreitai.smartbike.map.ext.toBitmapDescriptor
import com.kreitai.smartbike.map.presentation.StationMapVm
import kotlinx.android.synthetic.main.fragment_station_map.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class StationMapFragment :
    BaseFragment<StationMapVm, FragmentStationMapBinding>(),
    OnMapReadyCallback {

    companion object {
        private const val INITIAL_ZOOM = 13.0f
        private const val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"
    }

    private var googleMap: GoogleMap? = null

    override val toolbarResId: Int
        get() = com.kreitai.smartbike.R.id.stations_toolbar
    override val toolbarTitle: CharSequence
        get() = getString(com.kreitai.smartbike.R.string.stations_title)
    override val layoutResource: Int
        get() = com.kreitai.smartbike.R.layout.fragment_station_map

    private val stationMapVm by viewModel<StationMapVm>()

    override fun bindViewModel(
        viewBinding: FragmentStationMapBinding,
        viewModel: StationMapVm
    ) {
        viewBinding.view = this
        viewBinding.vm = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }
        map.onCreate(mapViewBundle)
        map.getMapAsync(this)
        getViewModel().getRenderedState()
            .observe(viewLifecycleOwner, Observer { stationsViewState ->

                stationsViewState.stations?.let { stationItems ->
                    for (station in stationItems) {
                        googleMap?.addMarker(
                            MarkerOptions().position(
                                LatLng(
                                    station.latitude,
                                    station.longitude
                                )
                            ).title("Marker in Taipei").icon(
                                context?.let {
                                    com.kreitai.smartbike.R.drawable.ic_bike.toBitmapDescriptor(
                                        it
                                    )
                                }
                            )
                        )
                    }
                }
            })
    }

    override fun getViewModel(): StationMapVm {
        return stationMapVm
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        this.googleMap = googleMap
        val taipei = LatLng(25.033, 121.52)
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(taipei, INITIAL_ZOOM))
        getViewModel().fetchStations()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        var mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle)
        }

        map.onSaveInstanceState(mapViewBundle)
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onStart() {
        super.onStart()
        map.onStart()
    }

    override fun onStop() {
        super.onStop()
        map.onStop()
    }

    override fun onPause() {
        map.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        map.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        map.onLowMemory()
    }

}
