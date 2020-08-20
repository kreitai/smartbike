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

package com.kreitai.orangebikes.about

import com.kreitai.orangebikes.BuildConfig
import com.kreitai.orangebikes.R
import com.kreitai.orangebikes.core.view.BaseFragment
import com.kreitai.orangebikes.databinding.FragmentAboutBinding
import com.kreitai.orangebikes.map.presentation.AboutVm
import org.koin.androidx.viewmodel.ext.android.viewModel

class AboutFragment : BaseFragment<AboutVm, FragmentAboutBinding>() {

    private val aboutVm by viewModel<AboutVm>()

    override val layoutResource: Int
        get() = R.layout.fragment_about

    override fun bindViewModel(viewBinding: FragmentAboutBinding, viewModel: AboutVm) {
        viewBinding.vm = viewModel
        viewBinding.appVersion = BuildConfig.VERSION_NAME
    }

    override fun getViewModel(): AboutVm {
        return aboutVm
    }


}