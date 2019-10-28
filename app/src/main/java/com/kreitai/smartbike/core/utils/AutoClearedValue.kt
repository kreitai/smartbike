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

package com.kreitai.smartbike.core.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

/**
 * A value holder that automatically clears the reference if the Fragment's view is destroyed.
 *
 * @param callback optional callback to perform additional actions on value clear
 */
class AutoClearedValue<VALUE>(
    fragment: Fragment,
    private var value: VALUE?,
    callback: (() -> Unit)?
) {

    constructor(fragment: Fragment, value: VALUE?) : this(fragment, value, null)

    init {
        val fragmentManager = fragment.fragmentManager
        fragmentManager!!.registerFragmentLifecycleCallbacks(
            object : FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
                    if (f === fragment) {
                        callback?.invoke()
                        this@AutoClearedValue.value = null
                        fragmentManager.unregisterFragmentLifecycleCallbacks(this)
                    }
                }
            }, false
        )
    }

    fun get(): VALUE? {
        return value
    }

}
