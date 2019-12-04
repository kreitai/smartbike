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

package com.kreitai.smartbike.core

import com.kreitai.smartbike.core.dispatcher.StationsDispatcherImpl
import com.kreitai.smartbike.core.domain.StationsResult
import com.kreitai.smartbike.core.remote.StationsRepository
import com.kreitai.smartbike.core.state.ReducerImpl
import com.kreitai.smartbike.core.state.StateHolderImpl
import com.kreitai.smartbike.core.viewmodel.Localization
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock

open class MviTest {

    private val testContextProvider = TestContextProvider()
    protected val mockLocalization: Localization = mock {}
    protected val mockStationsRepository: StationsRepository = mock {
        onBlocking { getStations() }.doReturn(StationsResult.Success(emptyList()))
    }

    protected val dispatcher = StationsDispatcherImpl(mockStationsRepository, testContextProvider)
    protected val reducer = ReducerImpl()
    protected val stateHolder = StateHolderImpl(dispatcher, reducer)

}
