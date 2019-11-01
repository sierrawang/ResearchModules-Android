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

package org.sagebionetworks.research.modules.psorcast.org.sagebionetworks.research.modules.psorcast.step.photo_display

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.view.View
import androidx.annotation.NonNull
import kotlinx.android.synthetic.main.srpm_show_photo_display_step_fragment.rs2_image_view
import org.sagebionetworks.research.mobile_ui.show_step.view.ShowStepFragmentBase
import org.sagebionetworks.research.mobile_ui.show_step.view.ShowUIStepFragmentBase
import org.sagebionetworks.research.mobile_ui.show_step.view.view_binding.UIStepViewBinding
import org.sagebionetworks.research.mobile_ui.widget.ActionButton
import org.sagebionetworks.research.modules.psorcast.R
import org.sagebionetworks.research.modules.psorcast.result.JointPhotographyResult
import org.sagebionetworks.research.modules.psorcast.step.photo_display.PhotoDisplayStepView
import org.sagebionetworks.research.presentation.model.action.ActionType
import org.sagebionetworks.research.presentation.model.interfaces.StepView

class ShowPhotoDisplayStepFragment :
        ShowUIStepFragmentBase<PhotoDisplayStepView, ShowPhotoDisplayStepViewModel, UIStepViewBinding<PhotoDisplayStepView>>() {

    private lateinit var photoFilePath : String

    companion object {
        @JvmStatic
        fun newInstance(@NonNull stepView: StepView): ShowPhotoDisplayStepFragment {
            val fragment = ShowPhotoDisplayStepFragment()
            val arguments = ShowStepFragmentBase.createArguments(stepView)
            fragment.arguments = arguments
            return fragment
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.srpm_show_photo_display_step_fragment
    }

    override fun instantiateAndBindBinding(view: View?): UIStepViewBinding<PhotoDisplayStepView> {
        return UIStepViewBinding(view)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var identifier = stepView.identifier.replace("Verify", "")
        var photoResult = this.performTaskViewModel.taskResult.getResult(identifier)
        if (photoResult is JointPhotographyResult) {
            photoFilePath = photoResult.photoAbsolutePath
            val bitmap = BitmapFactory.decodeFile(photoFilePath)
            // This seems unnecessary but haven't determined why picture is rotated
            var matrix = Matrix()
            matrix.postRotate(90f)
            val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            rs2_image_view.setImageBitmap(rotatedBitmap)
        }
    }

    override fun handleActionButtonClick(actionButton: ActionButton) {
        @ActionType val actionType = this.getActionTypeFromActionButton(actionButton)
        if (ActionType.FORWARD == actionType) {
            showStepViewModel.pdResultBuilder.setPhotoAbsolutePath(photoFilePath)
        }
        super.handleActionButtonClick(actionButton)
    }
}