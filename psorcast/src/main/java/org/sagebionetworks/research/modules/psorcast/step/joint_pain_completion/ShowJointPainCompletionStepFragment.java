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

package org.sagebionetworks.research.modules.psorcast.step.joint_pain_completion;

import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.sagebionetworks.research.domain.result.interfaces.Result;
import org.sagebionetworks.research.domain.result.interfaces.TaskResult;
import org.sagebionetworks.research.mobile_ui.show_step.view.ShowStepFragmentBase;
import org.sagebionetworks.research.mobile_ui.show_step.view.ShowUIStepFragmentBase;
import org.sagebionetworks.research.mobile_ui.show_step.view.view_binding.UIStepViewBinding;
import org.sagebionetworks.research.modules.psorcast.R;
import org.sagebionetworks.research.modules.psorcast.result.JointPainResult;
import org.sagebionetworks.research.modules.psorcast.step.joint_pain.JointPlacement;
import org.sagebionetworks.research.presentation.model.interfaces.StepView;
import org.sagebionetworks.research.presentation.show_step.show_step_view_models.ShowUIStepViewModel;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ShowJointPainCompletionStepFragment extends
        ShowUIStepFragmentBase<JointPainCompletionStepView, ShowUIStepViewModel<JointPainCompletionStepView>, UIStepViewBinding<JointPainCompletionStepView>> {

    private String[] bodyRegions = {"upperBody","lowerBody","leftHand","rightHand","leftFoot","rightFoot"};

    @NonNull
    public static ShowJointPainCompletionStepFragment newInstance(@NonNull StepView stepView) {
        if (!(stepView instanceof JointPainCompletionStepView)) {
            throw new IllegalArgumentException("Step view: " + stepView + " is not a JointPainCompletionStepView.");
        }

        ShowJointPainCompletionStepFragment fragment = new ShowJointPainCompletionStepFragment();
        Bundle arguments = ShowStepFragmentBase.createArguments(stepView);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.srpm_show_joint_pain_completion_step_fragment;
    }

    @NonNull
    @Override
    protected UIStepViewBinding<JointPainCompletionStepView> instantiateAndBindBinding(final View view) {
        return new UIStepViewBinding<>(view);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setResults(view, this.stepView.getJoints(), this.performTaskViewModel.getTaskResult());
    }

    private void setResults(View view, JointPlacement joints, TaskResult taskResult) {
        Set<Pair<Float,Float>> result = new HashSet<>();
        int count = 0;
        String horizontalBias = "horizontalBias";
        String verticalBias = "verticalBias";

        for (String region : bodyRegions) {
            Result regionResult = taskResult.getResult(region);
            if (regionResult instanceof JointPainResult) {
                Map<String, Boolean> selectedJoints = ((JointPainResult) regionResult).getSelectedJoints();
                for (String joint : selectedJoints.keySet()) {
                    if (selectedJoints.get(joint)) {
                        // Increment selected joints count
                        count++;

                        // Determine which joint to highlight
                        String key = joint;
                        if (!joints.coordinates.containsKey(key)) {
                            // Either finger or toe
                            if (region.equals("leftHand")) {
                                key = "left_wrist";
                            } else if (region.equals("rightHand")) {
                                key = "right_wrist";
                            } else if (region.equals("leftFoot")) {
                                key = "left_toes";
                            } else {
                                key = "right_toes";
                            }
                        }

                        Map<String,Float> coords = joints.coordinates.get(key);
                        Float x = coords.get(horizontalBias)/joints.widthScale;
                        Float y = coords.get(verticalBias)/joints.heightScale;
                        result.add(new Pair<>(x,y));
                    }
                }
            }
        }

        setJoints(view.findViewById(R.id.rs2_image_view), result, joints.buttonSize);
        setLabel(view.findViewById(R.id.joint_count), count);
    }

    private void setJoints(JointPainCompletionImageView image, Set<Pair<Float,Float>> joints, Float buttonSize) {
        image.setJoints(joints);
        image.setRadius(buttonSize/2);
    }

    private void setLabel(TextView label, int count) {
        label.setText("" + count);
    }

}
