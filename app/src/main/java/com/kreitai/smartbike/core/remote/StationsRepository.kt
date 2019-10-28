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

package com.kreitai.smartbike.core.remote

import com.kreitai.smartbike.core.domain.StationsResult
import com.kreitai.smartbike.core.remote.model.Station
import java.nio.charset.Charset

class StationsRepository constructor(private val serviceProvider: ServiceProvider) {

    suspend fun getStations(): StationsResult {
        abortFetch()
        val response = doGetStations()
        return if (response is NetworkResult.Success) {
            StationsResult.Success(response.data)
        } else {
            StationsResult.Failure(response.toString())
        }
    }

    private suspend fun doGetStations(): NetworkResult<List<Station>?> {
        val response = serviceProvider.service.getStationsAsync("en").await()
        if (response.isSuccessful) {
            val stationsResponse = response.body()
            val data = stationsResponse?.stations
            return if (data.isNullOrEmpty()) {
                NetworkResult.Failure(
                    "Failed to retrieve result."
                )
            } else {
                NetworkResult.Success(data)
            }
        } else {
            return NetworkResult.Failure(
                response.errorBody()?.bytes()?.toString(Charset.forName("UTF-8"))
                    ?: "Failed to retrieve result."
            )
        }
    }

    private fun abortFetch() {
        serviceProvider.cancelAllRequests()
    }
}