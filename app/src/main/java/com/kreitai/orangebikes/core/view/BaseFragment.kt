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

package com.kreitai.orangebikes.core.view

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.kreitai.orangebikes.R
import com.kreitai.orangebikes.core.viewmodel.SmartBikeViewModel
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf


abstract class BaseFragment<VIEWMODEL : SmartBikeViewModel<*>, VIEWBINDING : ViewDataBinding> :
    DataBindingFragment<VIEWMODEL, VIEWBINDING>() {

    private val dialogController: DialogController by inject { parametersOf(this@BaseFragment) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getViewModel().renderedState
            .observe(viewLifecycleOwner, Observer { stationsViewState ->
                stationsViewState.error?.let {
                    dialogController.showMessage(
                        getString(R.string.general_error_dialog_title),
                        it,
                        getString(R.string.general_error_dialog_button),
                        DialogInterface.OnClickListener { _: DialogInterface, _: Int ->
                            getViewModel().retryLastAction()
                        },
                        DialogInterface.OnCancelListener {
                            getViewModel().resetAppState()
                            findNavController().popBackStack()
                        }
                    )
                }
            })
    }

}