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

package org.sagebionetworks.research.modules.psorcast.step.joint_photography

import org.sagebionetworks.research.modules.psorcast.result.JointPhotographyResult
import org.sagebionetworks.research.presentation.model.action.ActionType
import org.sagebionetworks.research.presentation.perform_task.PerformTaskViewModel
import org.sagebionetworks.research.presentation.show_step.show_step_view_model_factories.ShowStepViewModelFactory
import org.sagebionetworks.research.presentation.show_step.show_step_view_models.ShowStepViewModel
import org.sagebionetworks.research.presentation.show_step.show_step_view_models.ShowUIStepViewModel
import org.threeten.bp.ZonedDateTime

class ShowJointPhotographyStepViewModel(performTaskViewModel: PerformTaskViewModel,
        jointPhotographyStepView: JointPhotographyStepView) :
        ShowUIStepViewModel<JointPhotographyStepView>(performTaskViewModel, jointPhotographyStepView) {

    val jpResultBuilder : JointPhotographyResult.Builder

    init {
        val zonedStart = ZonedDateTime.now()
        jpResultBuilder = JointPhotographyResult.builder()
                .setStartTime(zonedStart.toInstant())
                .setIdentifier(stepView.identifier)
    }

    override fun handleAction(actionType: String) {
        if (actionType == ActionType.FORWARD) {
            this.performTaskViewModel.addStepResult(jpResultBuilder.build())
        }
        super.handleAction(actionType)
    }
}

class ShowJointPhotographyStepViewModelFactory :
        ShowStepViewModelFactory<ShowJointPhotographyStepViewModel, JointPhotographyStepView> {

    override fun create(performTaskViewModel: PerformTaskViewModel,
            stepView: JointPhotographyStepView): ShowJointPhotographyStepViewModel {
        return ShowJointPhotographyStepViewModel(performTaskViewModel, stepView)
    }

    override fun getViewModelClass(): Class<out ShowStepViewModel<*>> {
        return ShowJointPhotographyStepViewModel::class.java
    }
}