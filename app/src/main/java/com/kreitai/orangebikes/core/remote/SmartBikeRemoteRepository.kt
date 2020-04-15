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

package com.kreitai.orangebikes.core.remote

import kotlinx.coroutines.Deferred
import retrofit2.Response
import java.io.IOException
import java.net.UnknownHostException
import java.nio.charset.Charset

abstract class SmartBikeRemoteRepository<RESULT>(private val serviceProvider: ServiceProvider) {

    protected suspend fun fetchData(): NetworkResult<RESULT> {
        abortFetch()
        try {
            val response = getDeferredResponseAsync(serviceProvider).await()
            return if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody == null) {
                    NetworkResult.Failure(
                        "Failed to retrieve result."
                    )
                } else {
                    NetworkResult.Success(responseBody)
                }
            } else {
                return try {
                    NetworkResult.Failure(
                        response.errorBody()?.bytes()?.toString(Charset.forName("UTF-8")) ?: ""
                    )
                } catch (e: IOException) {
                    NetworkResult.Failure("Failed to retrieve result: $e")
                }
            }
        } catch (e: UnknownHostException) {
            e.printStackTrace()
            return NetworkResult.Failure(
                "Could not load the data. Please check that your device is connected to the internet."
            )
        }
    }

    abstract fun getDeferredResponseAsync(serviceProvider: ServiceProvider): Deferred<Response<RESULT>>

    private fun abortFetch() {
        serviceProvider.cancelAllRequests()
    }

}
