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

package com.kreitai.orangebikes.map.presentation

import android.location.Location
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.kreitai.orangebikes.R
import com.kreitai.orangebikes.core.dispatcher.StationsDispatcher
import com.kreitai.orangebikes.core.domain.action.StationsAction
import com.kreitai.orangebikes.core.remote.model.Station
import com.kreitai.orangebikes.core.state.AppState
import com.kreitai.orangebikes.core.state.StateHolder
import com.kreitai.orangebikes.core.view.StationsViewState
import com.kreitai.orangebikes.core.viewmodel.Localization
import com.kreitai.orangebikes.core.viewmodel.SmartBikeViewModel
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class StationMapVm(
    stateHolder: StateHolder,
    dispatcher: StationsDispatcher,
    private val localization: Localization
) :
    SmartBikeViewModel<StationsViewState>(stateHolder, dispatcher) {
    val isWalkingMode = MutableLiveData(true)
    val youBikeType = MutableLiveData(1)

    override fun render(appState: AppState): StationsViewState? {
        return StationsViewState(
            appState.isLoading,
            appState.stations?.filter(fun(station: Station): Boolean {
                if (station.latitude.isNullOrEmpty() || station.longitude.isNullOrEmpty()) {
                    return false
                }
                try {
                    station.latitude.toDouble()
                    station.longitude.toDouble()
                } catch (e: NumberFormatException) {
                    return false
                }
                return true
            })
                ?.map {
                    StationItem(
                        localization.localizeStationName(it.englishName, it.mandarinName),
                        LatLng(
                            it.latitude?.toDouble() ?: 0.0,
                            it.longitude?.toDouble() ?: 0.0
                        ),
                        localization.localize(R.string.available_bikes, it.availableSpaces),
                        when (it.ratio) {
                            0.0 -> StationItem.StationState.EMPTY
                            in 0.0..0.1 -> StationItem.StationState.LOW
                            else -> StationItem.StationState.MANY
                        }
                    )
                },
            error = if (!appState.isLoading && appState.stations.isNullOrEmpty()) "No more unique stations!" else null
        )
    }

    fun fetchStations() {
        dispatcher.nextAction.value =
            youBikeType.value?.let { StationsAction.GetStationsAction(it) }
    }

    fun findNearestStation(
        event: Location,
        stations: List<StationItem>?
    ): LatLng? {
        if (stations.isNullOrEmpty()) {
            return null
        }

        val lat = event.latitude
        val lng = event.longitude
        val r = 6371 // radius of earth in km
        val distances = DoubleArray(stations.size)
        var closest = -1
        for (i in stations.indices) {
            val mlat = stations[i].latLng.latitude
            val mlng = stations[i].latLng.longitude
            val dLat = Math.toRadians(mlat - lat)
            val dLong = Math.toRadians(mlng - lng)
            val a = sin(dLat / 2) * sin(dLat / 2) +
                    cos(Math.toRadians(lat)) * cos(Math.toRadians(lat)) * sin(dLong / 2) * sin(dLong / 2)
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))
            val d = r * c
            distances[i] = d
            if (closest == -1 || d < distances[closest]) {
                closest = i
            }
        }

        return stations[closest].latLng
    }

    /**
     * Toggle YouBike type - v1/v2
     */
    fun toggleBikeType() {
        youBikeType.value?.let { youBikeType.value = it % 2 + 1 }
        fetchStations()
    }

    /**
     * Toggle camera mode between walking (freehand pan & zoom) and cycling (camera automatically follows
     * user's location and always keeps the user and the nearest station positions in the map frame).
     */
    fun toggleMode() {
        isWalkingMode.value?.let { isWalkingMode.value = !it }
    }
}
