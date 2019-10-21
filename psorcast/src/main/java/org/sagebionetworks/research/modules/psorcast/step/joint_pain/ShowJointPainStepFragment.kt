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

package org.sagebionetworks.research.modules.psorcast.step.joint_pain

import androidx.lifecycle.Observer
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.sagebionetworks.research.mobile_ui.show_step.view.ShowStepFragmentBase
import org.sagebionetworks.research.mobile_ui.show_step.view.ShowUIStepFragmentBase
import org.sagebionetworks.research.mobile_ui.show_step.view.view_binding.UIStepViewBinding
import org.sagebionetworks.research.modules.psorcast.R
import org.sagebionetworks.research.presentation.model.interfaces.StepView
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.ImageView
import android.widget.ToggleButton
import kotlinx.android.synthetic.main.srpm_show_joint_pain_step_fragment.rs2_text
import org.sagebionetworks.research.modules.psorcast.R.drawable

class ShowJointPainStepFragment : ShowUIStepFragmentBase<JointPainStepView, ShowJointPainStepViewModel, UIStepViewBinding<JointPainStepView>>() {
    companion object {
        @JvmStatic
        fun newInstance(@NonNull stepView: StepView): ShowJointPainStepFragment {
            val fragment = ShowJointPainStepFragment()
            val arguments = ShowStepFragmentBase.createArguments(stepView)
            fragment.arguments = arguments
            return fragment
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.srpm_show_joint_pain_step_fragment
    }

    override fun instantiateAndBindBinding(view: View?): UIStepViewBinding<JointPainStepView> {
        return UIStepViewBinding(view)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var v = super.onCreateView(inflater, container, savedInstanceState)

        // Add a listener to draw the joint buttons once the layout has been drawn
        if (v != null) {
            var viewTreeObserver = v.viewTreeObserver
            if (viewTreeObserver.isAlive) {
                viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        v.getViewTreeObserver().removeOnGlobalLayoutListener(this)
                        addButtons()
                    }
                })
            }
        }

        // Listen for changes in the selected joints
        this.showStepViewModel.jointCount.observe(this, Observer {
            numJoints ->
            if (numJoints == null || numJoints == 0) {
                this.rs2_text.text = "Select all that apply"
            } else if (numJoints == 1) {
                this.rs2_text.text = "1 joint selected"
            } else {
                this.rs2_text.text = "" + numJoints + " joints selected"
            }
        })

        return v
    }

    private fun addButtons() {
        val horizontalBias = "horizontalBias"
        val verticalBias = "verticalBias"
        var iv = this.stepViewBinding.imageView
        if (view is ConstraintLayout && iv != null && this.stepView.joints != null) {
            val joints = this.stepView.joints
            val buttonSize = joints.buttonSize/joints.heightScale * iv.measuredHeight

            // Add a button for each joint for this view
            for (joint in joints.coordinates) {
                val name = joint.key
                val coords = joint.value

                var buttonParams = LayoutParams(buttonSize.toInt(), buttonSize.toInt())
                // Anchor the button to the imageview
                buttonParams.topToTop = iv.id
                buttonParams.leftToLeft = iv.id
                buttonParams.bottomToBottom = iv.id
                buttonParams.rightToRight = iv.id

                // Define the position of the button
                buttonParams.horizontalBias = coords[horizontalBias]!!/joints.widthScale
                buttonParams.horizontalBias = newHorizontalBias(iv, buttonParams.horizontalBias)
                buttonParams.horizontalBias = adjustForButtonSize(buttonParams.horizontalBias, iv.measuredWidth.toFloat(), buttonSize)
                buttonParams.verticalBias = coords[verticalBias]!!/joints.heightScale
                buttonParams.verticalBias = adjustForButtonSize(buttonParams.verticalBias, iv.measuredHeight.toFloat(), buttonSize)

                // Define button for this joint
                var button = ToggleButton(context)
                button.tag = name
                button.layoutParams = buttonParams
                button.textOff = ""
                button.textOn = ""
                button.text = ""
                button.setBackgroundResource(drawable.srpm_joint_pain_toggle_button)

                // Add the button to the view
                (view as ConstraintLayout).addView(button)

                // Initialize value in view model and forward the button click to the view model.
                this.showStepViewModel.selectedJoints[name] = button.isChecked
                button.setOnClickListener {
                    this.showStepViewModel.handleJointPress(name, button.isChecked)
                }
            }
        }
    }

    private fun adjustForButtonSize(bias: Float, length: Float, buttonSize: Float): Float {
        return (bias*length - buttonSize/2f)/(length - buttonSize)
    }

    private fun newHorizontalBias(iv: ImageView, oldScale: Float) : Float {
        var viewWidth = iv.measuredWidth.toFloat()
        var imgRatio = iv.drawable.intrinsicWidth.toFloat()/iv.drawable.intrinsicHeight
        var imgWidth = imgRatio * iv.measuredHeight.toFloat()
        var shift = (viewWidth - imgWidth)/2f
        var position = imgWidth * oldScale + shift
        var newScale = position/viewWidth
        return newScale
    }
}