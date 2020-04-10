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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.kreitai.orangebikes.core.utils.AutoClearedValue
import com.kreitai.orangebikes.core.viewmodel.SmartBikeViewModel

abstract class DataBindingFragment<VIEWMODEL : SmartBikeViewModel, VIEWBINDING : ViewDataBinding> :
    Fragment() {

    private lateinit var binding: AutoClearedValue<VIEWBINDING>

    protected abstract val layoutResource: Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding: VIEWBINDING =
            DataBindingUtil.inflate(inflater, layoutResource, container, false)
        dataBinding.lifecycleOwner = this
        binding = AutoClearedValue(this, dataBinding)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = getViewModel()

        val binding = binding.get() ?: throw IllegalStateException("Binding was released")
        bindViewModel(binding, viewModel)
    }

    abstract fun bindViewModel(viewBinding: VIEWBINDING, viewModel: VIEWMODEL)

    abstract fun getViewModel(): VIEWMODEL

    protected fun getViewBinding(): VIEWBINDING {
        val view = view ?: throw IllegalStateException("Fragment was released")
        return DataBindingUtil.getBinding(view) ?: throw IllegalStateException("No binding")
    }

}