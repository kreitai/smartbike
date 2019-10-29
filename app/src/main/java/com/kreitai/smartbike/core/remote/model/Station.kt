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

package com.kreitai.smartbike.core.remote.model

import com.google.gson.annotations.SerializedName

data class Station(
    @SerializedName("lat")
    val latitude: Double?,
    @SerializedName("lng")
    val longitude: Double?,
    @SerializedName("available_spaces")
    val availableSpaces: Int?,
    @SerializedName("empty_spaces")
    val emptySpaces: Int?,
    @SerializedName("name_tw")
    val mandarinName: String,
    @SerializedName("name_en")
    val englishName: String,
    @SerializedName("img")
    val image: String
) {
    val ratio get() = (this.availableSpaces?.div((this.emptySpaces ?: 1).toDouble())) as Double
}
/*{
         "country_code":"00",
         "area_code":"00",
         "type":1,
         "status":1,
         "station_no":"0002",
         "name_tw":"捷運國父紀念館站(2號出口)",
         "district_tw":"大安區",
         "address_tw":"忠孝東路四段/光復南路口(西南側)",
         "parking_spaces":48,
         "available_spaces":5,
         "empty_spaces":42,
         "forbidden_spaces":0,
         "lat":"25.041254",
         "lng":"121.55742",
         "img":"/images/station/default.jpg",
         "updated_at":"2019-10-25 15:58:04",
         "time":"2019-10-25 15:57:32",
         "name_en":"MRT S.Y.S Memorial Hall Stataion(Exit 2.)",
         "district_en":"Daan Dist.",
         "address_en":"Sec,4. Zhongxiao E.Rd/GuangFu S. Rd"
      },*/