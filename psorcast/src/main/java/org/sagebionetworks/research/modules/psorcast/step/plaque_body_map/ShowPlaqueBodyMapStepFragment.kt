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
import androidx.annotation.NonNull
import android.view.View
import kotlinx.android.synthetic.main.srpm_show_plaque_body_step_fragment.rs2_image_view
import kotlinx.android.synthetic.main.srpm_show_plaque_body_step_fragment.view.rs2_image_view
import org.sagebionetworks.research.mobile_ui.show_step.view.ShowStepFragmentBase
import org.sagebionetworks.research.mobile_ui.show_step.view.ShowUIStepFragmentBase
import org.sagebionetworks.research.mobile_ui.show_step.view.view_binding.UIStepViewBinding
import org.sagebionetworks.research.mobile_ui.widget.ActionButton
import org.sagebionetworks.research.modules.psorcast.R
import org.sagebionetworks.research.presentation.model.action.ActionType
import org.sagebionetworks.research.presentation.model.interfaces.StepView
import org.slf4j.Logger
import org.slf4j.LoggerFactory

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
            setBitmap()
        }
        super.handleActionButtonClick(actionButton)
    }

    private fun setBitmap() {
        val v = view as View
        val bitmap = loadBitmapFromView(v, v.rs2_image_view.width, v.rs2_image_view.height)
        this.showStepViewModel.pdResultBuilder.setBitmap(bitmap)
    }

    private fun loadBitmapFromView(v: View, width: Int, height: Int): Bitmap {
        var b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        var c = Canvas(b)
        v.layout(0, 0, width, height)
        v.draw(c)
        return b
    }

}
