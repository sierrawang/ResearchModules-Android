/*
 * BSD 3-Clause License
 *
 * Copyright 2019  Sage Bionetworks. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1.  Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2.  Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * 3.  Neither the name of the copyright holder(s) nor the names of any contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission. No license is granted to the trademarks of
 * the copyright holders even if such marks are included in this software.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.sagebionetworks.research.modules.psorcast.step.plaque_body_map

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.NonNull
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.srpm_show_plaque_body_step_fragment.rs2_image_view
import kotlinx.android.synthetic.main.srpm_show_plaque_body_step_fragment.view.rs2_image_view
import org.sagebionetworks.research.domain.result.interfaces.TaskResult
import org.sagebionetworks.research.mobile_ui.show_step.view.ShowStepFragmentBase
import org.sagebionetworks.research.mobile_ui.show_step.view.ShowUIStepFragmentBase
import org.sagebionetworks.research.mobile_ui.show_step.view.view_binding.UIStepViewBinding
import org.sagebionetworks.research.mobile_ui.widget.ActionButton
import org.sagebionetworks.research.modules.psorcast.PlaqueCoverageView
import org.sagebionetworks.research.modules.psorcast.R
import org.sagebionetworks.research.presentation.model.FetchableImageThemeView
import org.sagebionetworks.research.presentation.model.action.ActionType
import org.sagebionetworks.research.presentation.model.interfaces.StepView
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.TreeMap

class ShowPlaqueBodyMapStepFragment :
        ShowUIStepFragmentBase<PlaqueBodyMapStepView, ShowPlaqueBodyMapStepViewModel, UIStepViewBinding<PlaqueBodyMapStepView>>() {

    companion object {
        @JvmStatic
        fun newInstance(@NonNull stepView: StepView): ShowPlaqueBodyMapStepFragment {
            val fragment = ShowPlaqueBodyMapStepFragment()
            val arguments = ShowStepFragmentBase.createArguments(stepView)
            fragment.arguments = arguments
            return fragment
        }
    }

    private var logger: Logger = LoggerFactory.getLogger(ShowPlaqueBodyMapStepFragment::class.java)

    override fun getLayoutId(): Int {
        return R.layout.srpm_show_plaque_body_step_fragment
    }

    override fun instantiateAndBindBinding(view: View?): UIStepViewBinding<PlaqueBodyMapStepView> {
        return UIStepViewBinding(view)
    }

    override fun handleActionButtonClick(actionButton: ActionButton) {
        @ActionType val actionType = this.getActionTypeFromActionButton(actionButton)
        if (ActionType.FORWARD == actionType) {
            this.showStepViewModel.pdResultBuilder.setPaths(this.rs2_image_view.getPaths())
            val bitmap = loadBitmapFromView()
            this.showStepViewModel.pdResultBuilder.setBitmap(bitmap)
            val pixelCounts = getPixelCounts(bitmap)
            this.showStepViewModel.pdResultBuilder.setCoveredPixels(pixelCounts.first)
            this.showStepViewModel.pdResultBuilder.setTotalPixels(pixelCounts.second)
        }
        super.handleActionButtonClick(actionButton)
    }

    private fun loadBitmapFromView(): Bitmap {
        val v = (view as View).rs2_image_view
        val width = v.width
        val height = v.height

        // Make copy of the view
        var b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        var c = Canvas(b)
        v.draw(c)

        // Trim the bitmap to the dimensions of the drawable
        var drawableHeight : Int
        var drawableWidth : Int
        if (v.drawable.intrinsicHeight.toFloat()/v.drawable.intrinsicWidth > height.toFloat()/width) {
            // Scaled to height
            drawableHeight = height
            drawableWidth = (v.drawable.intrinsicWidth * (height.toFloat() / v.drawable.intrinsicHeight)).toInt()
        } else {
            drawableWidth = width
            drawableHeight = (v.drawable.intrinsicHeight * (width.toFloat() / v.drawable.intrinsicWidth)).toInt()
        }

        val padLeft = (width - drawableWidth)/2
        val padTop = (height - drawableHeight)/2
        val result = Bitmap.createBitmap(b, padLeft, padTop, drawableWidth, drawableHeight)

        return result
    }

    private fun getPixelCounts(bitmap: Bitmap): Pair<Int,Int> {
        var coveredPixels = 0
        var totalPixels = 0
        val highlightColor = ContextCompat.getColor(context!!, R.color.colorAccent)

        for (row in 0 until bitmap.width) {
            for (col in 0 until bitmap.height) {
                val color = bitmap.getPixel(row,col)

                val r = Color.red(color)
                val g = Color.green(color)
                val b = Color.blue(color)
                if (color != Color.TRANSPARENT) {
                    totalPixels++
                    if (Color.rgb(r,g,b) == highlightColor) {
                        coveredPixels++
                    }
                }
            }
        }

        return Pair(coveredPixels, totalPixels)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var result = super.onCreateView(inflater, container, savedInstanceState)
        val backgroundImage = this.stepView.backgroundImage as FetchableImageThemeView
        if (result != null) {
            result.findViewById<ImageView>(R.id.background_image).setBackgroundResource(backgroundImage.imageResource?.drawable!!)
        }
        return result
    }
}
