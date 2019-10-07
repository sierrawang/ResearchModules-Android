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

package org.sagebionetworks.research.modules.psorcast.step.psorcast_active;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import org.sagebionetworks.research.mobile_ui.show_step.view.ShowActiveUIStepFragmentBase;
import org.sagebionetworks.research.mobile_ui.show_step.view.ShowStepFragmentBase;
import org.sagebionetworks.research.mobile_ui.show_step.view.view_binding.ActiveUIStepViewBinding;
import org.sagebionetworks.research.mobile_ui.widget.ActionButton;
import org.sagebionetworks.research.modules.psorcast.R;
import org.sagebionetworks.research.presentation.model.action.ActionType;
import org.sagebionetworks.research.presentation.model.action.ActionView;
import org.sagebionetworks.research.presentation.model.interfaces.StepView;
import org.sagebionetworks.research.presentation.model.interfaces.UIStepView;
import org.sagebionetworks.research.presentation.show_step.show_step_view_models.ShowActiveUIStepViewModel;

public class ShowPsorcastActiveUIStepFragment extends ShowActiveUIStepFragmentBase
        <PsorcastActiveUIStepView, ShowActiveUIStepViewModel<PsorcastActiveUIStepView>, ActiveUIStepViewBinding<PsorcastActiveUIStepView>>{
    @NonNull
    public static ShowPsorcastActiveUIStepFragment newInstance(@NonNull StepView stepView) {
        ShowPsorcastActiveUIStepFragment fragment = new ShowPsorcastActiveUIStepFragment();
        Bundle arguments = ShowStepFragmentBase.createArguments(stepView);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.srpm_psorcast_active_step;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!showStepViewModel.isCountdownRunning() && !showStepViewModel.isCountdownPaused()) {
            showStepViewModel.startCountdown();
        }
    }

    @NonNull
    @Override
    protected ActiveUIStepViewBinding<PsorcastActiveUIStepView> instantiateAndBindBinding(View view) {
        return new ActiveUIStepViewBinding<>(view);
    }

    @Override
    protected void update(PsorcastActiveUIStepView stepView) {
        super.update(stepView);
        ActionButton skipButton = this.stepViewBinding.getSkipButton();
        if (skipButton != null) {
            skipButton.setPaintFlags(skipButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }
    }

    @Override
    protected void updateNavigationButtons(UIStepView stepView) {
        super.updateNavigationButtons(stepView);
        // To keep parity with iOS' active ui step action mappings, but still have our navigation bar work,
        // we must re-wire the mapping so that the skip button is info or "learn more", and the next button is skip
        ActionButton forwardButton = this.stepViewBinding.getNextButton();
        ActionView skipActionView = this.getSkipButtonActionView(stepView);
        this.updateButtonFromActionView(forwardButton, skipActionView);
        ActionView infoActionView = this.getInfoButtonActionView(stepView);
        ActionButton skipButton = this.stepViewBinding.getSkipButton();
        this.updateButtonFromActionView(skipButton, infoActionView);
    }

    @Override
    @Nullable
    @ActionType
    protected String getActionTypeFromActionButton(@NonNull ActionButton actionButton) {
        int actionButtonId = actionButton.getId();

        // To keep parity with iOS' active ui step action mappings, but still have our navigation bar work,
        // we must re-wire the mapping so that the skip button is info or "learn more", and the next button is skip
        if (org.sagebionetworks.research.mobile_ui.R.id.rs2_step_navigation_action_forward == actionButtonId) {
            return ActionType.SKIP;
        } else if (org.sagebionetworks.research.mobile_ui.R.id.rs2_step_navigation_action_skip == actionButtonId) {
            return ActionType.INFO;
        }

        return super.getActionTypeFromActionButton(actionButton);
    }
}
