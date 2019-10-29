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

package com.kreitai.smartbike.map.presentation

import com.kreitai.smartbike.R
import com.kreitai.smartbike.core.dispatcher.StationsDispatcher
import com.kreitai.smartbike.core.domain.action.StationsAction
import com.kreitai.smartbike.core.state.AppState
import com.kreitai.smartbike.core.state.StateHolder
import com.kreitai.smartbike.core.view.StationsViewState
import com.kreitai.smartbike.core.viewmodel.Localization
import com.kreitai.smartbike.core.viewmodel.SmartBikeViewModel

class StationMapVm(
    stateHolder: StateHolder,
    dispatcher: StationsDispatcher,
    private val localization: Localization
) :
    SmartBikeViewModel(stateHolder, dispatcher) {

    override fun preRender(appState: AppState): StationsViewState? {
        return StationsViewState(
            appState.isLoading,
            appState.stations?.filter { station -> station.latitude != null && station.longitude != null }?.map {
                StationItem(
                    localization.localizeStationName(it.englishName, it.mandarinName),
                    it.latitude ?: 0.0,
                    it.longitude ?: 0.0,
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
        dispatcher.nextAction.value = StationsAction.GetStationsAction
    }

}
