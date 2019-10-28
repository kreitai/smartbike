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

package com.kreitai.smartbike.core.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.kreitai.smartbike.core.dispatcher.StationsDispatcher
import com.kreitai.smartbike.core.state.AppState
import com.kreitai.smartbike.core.state.StateHolder
import com.kreitai.smartbike.core.view.StationsViewState

abstract class SmartBikeViewModel(
    private val stateHolder: StateHolder,
    protected val dispatcher: StationsDispatcher
) :
    ViewModel() {

    fun getRenderedState(): LiveData<StationsViewState> {
        return Transformations.map(stateHolder.getAppState()) { appState ->
            preRender(appState).also { stationsViewState: StationsViewState? ->
                appState.error?.let { stationsViewState?.error = it }
            }
        }
    }

    abstract fun preRender(appState: AppState): StationsViewState?

    fun retryLastAction() {
        dispatcher.retryLastAction()
    }

    fun resetAppState() {
        stateHolder.resetState()
    }

}
