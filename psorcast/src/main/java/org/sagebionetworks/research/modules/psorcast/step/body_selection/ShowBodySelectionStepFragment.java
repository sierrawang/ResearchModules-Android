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

package org.sagebionetworks.research.modules.psorcast.step.body_selection;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;

import org.sagebionetworks.research.mobile_ui.show_step.view.FormUIStepFragment;
import org.sagebionetworks.research.mobile_ui.show_step.view.ShowStepFragmentBase;
import org.sagebionetworks.research.modules.psorcast.R;
import org.sagebionetworks.research.modules.psorcast.result.BodySelectionResult;
import org.sagebionetworks.research.presentation.model.form.ChoiceInputFieldViewBase;
import org.sagebionetworks.research.presentation.model.form.InputFieldView;
import org.sagebionetworks.research.presentation.model.interfaces.StepView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.Instant;

import java.util.HashSet;
import java.util.List;

public class ShowBodySelectionStepFragment extends FormUIStepFragment {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShowBodySelectionStepFragment.class);
    public static final String BODY_SELECTION_KEY = "bodySelection";

    @NonNull
    public static ShowBodySelectionStepFragment newInstance(@NonNull StepView stepView) {
        ShowBodySelectionStepFragment fragment = new ShowBodySelectionStepFragment();
        Bundle arguments = ShowStepFragmentBase.createArguments(stepView);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.srpm_show_body_selection_step_fragment;
    }

    public void writeBodySelectionResult(HashSet<String> bodySelection) {
        BodySelectionResult result = new BodySelectionResult(
                BODY_SELECTION_KEY, Instant.now(), Instant.now(), bodySelection);
        this.performTaskViewModel.addStepResult(result);
    }

    @Override
    protected void initializeRecyclerView() {
        // no-op, we do completely custom view
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = super.onCreateView(inflater, container, savedInstanceState);

        RecyclerView recyclerView = this.stepViewBinding.getRecyclerView();

        if (recyclerView != null) {
            List<InputFieldView> inputFields = stepView.getInputFields();
            if (inputFields.isEmpty()) {
                LOGGER.warn("Form step with no input fields created.");
                return result;
            } else if (inputFields.size() > 1) {
                LOGGER.warn("Form step with more than 1 input field created, using the first input field.");
            }

            InputFieldView inputField = inputFields.get(0);
            if (!(inputField instanceof ChoiceInputFieldViewBase<?>)) {
                LOGGER.warn("Form step with a non ChoiceInput field created.");
                return result;
            }

            ChoiceInputFieldViewBase<?> choiceInputField = (ChoiceInputFieldViewBase<?>) inputField;
            RecyclerAdapter adapter = new RecyclerAdapter(this, recyclerView,
                    choiceInputField.getChoices(), new HashSet<>());
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
        }

        return result;
    }
}
