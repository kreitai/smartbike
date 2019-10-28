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

package com.kreitai.smartbike.core.view

import android.content.DialogInterface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.kreitai.smartbike.R
import com.kreitai.smartbike.core.viewmodel.SmartBikeViewModel
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf


abstract class BaseFragment<VIEWMODEL : SmartBikeViewModel, VIEWBINDING : ViewDataBinding> :
    DataBindingFragment<VIEWMODEL, VIEWBINDING>() {

    protected abstract val toolbarResId: Int
    protected abstract val toolbarTitle: CharSequence
    protected open val toolbarTitleGravity = Gravity.CENTER
    protected open val toolbarShowBackButton = true
    private val dialogController: DialogController by inject { parametersOf(this@BaseFragment) }

    private var toolbar: Toolbar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Toolbar>(toolbarResId)?.run {
            toolbar = this
            setupToolbar(toolbarShowBackButton)
        }

        getViewModel().getRenderedState()
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

    private fun setupToolbar(toolbarShowBackButton: Boolean) {

        if (this.toolbar == null) return
        val toolbar: Toolbar = this.toolbar!!

        val title = toolbar.findViewById<TextView>(R.id.toolbar_title)

        val params = title?.layoutParams as Toolbar.LayoutParams
        params.gravity = toolbarTitleGravity
        title.layoutParams = params

        val mainActivity = activity as MainActivity
        mainActivity.setupToolbar(toolbar) {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(toolbarShowBackButton)
            title.text = toolbarTitle
        }
    }

}