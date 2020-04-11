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

package com.kreitai.orangebikes.core.viewmodel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.kreitai.orangebikes.core.dispatcher.StationsDispatcher
import com.kreitai.orangebikes.core.state.AppState
import com.kreitai.orangebikes.core.state.StateHolder
import com.kreitai.orangebikes.core.view.BaseViewState

abstract class SmartBikeViewModel<T : BaseViewState>(
    private val stateHolder: StateHolder,
    protected val dispatcher: StationsDispatcher
) :
    ViewModel() {

    val renderedState = MediatorLiveData<T>().apply {
        this.addSource(
            Transformations.map(stateHolder.getAppState()) { appState ->
                preRender(appState).also { viewState: T? ->
                    appState.error?.let { viewState?.error = it }
                }
            }
        ) {
            value = it
        }
    }

    abstract fun preRender(appState: AppState): T?

    fun retryLastAction() {
        dispatcher.retryLastAction()
    }

    fun resetAppState() {
        stateHolder.resetState()
    }

}
