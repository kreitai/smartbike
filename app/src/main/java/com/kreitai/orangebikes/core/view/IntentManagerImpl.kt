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

package com.kreitai.orangebikes.core.view

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.kreitai.orangebikes.core.utils.IntentManager

class IntentManagerImpl(private val fragment: Fragment) : IntentManager {

    override fun openInBrowser(url: String?) {
        if (url.isNullOrEmpty()) {
            return
        }
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url.trim()))
        if (!canHandleIntent(intent)) {
            Log.e(javaClass.simpleName, "Couldn't handle intent for URL $url")
            return
        }
        fragment.startActivity(intent)
    }

    private fun canHandleIntent(intent: Intent): Boolean {
        val manager = fragment.requireActivity().packageManager
        val infos = manager.queryIntentActivities(intent, 0)
        return infos.size > 0
    }
}