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

import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.kreitai.smartbike.BuildConfig
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceFactory {

    fun createService(
        gson: Gson,
        baseUrl: String,
        dispatcher: Dispatcher
    ): SmartBikeService {
        val okHttpClient = createOkHttpClient(
            createLoggingInterceptor(),
            dispatcher

        )
        return createService(okHttpClient, gson, baseUrl).create(SmartBikeService::class.java)
    }

    private fun createService(
        okHttpClient: OkHttpClient,
        gson: Gson,
        baseUrl: String
    ): Retrofit {
        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
        retrofit.baseUrl(baseUrl)
        return retrofit.build()
    }

    private fun createOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        dispatcher: Dispatcher
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .followRedirects(false)
            .dispatcher(dispatcher)

        return builder.build()
    }

    private fun createLoggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.level =
            if (!BuildConfig.DEBUG) HttpLoggingInterceptor.Level.NONE else HttpLoggingInterceptor.Level.BODY
        return logging
    }
}