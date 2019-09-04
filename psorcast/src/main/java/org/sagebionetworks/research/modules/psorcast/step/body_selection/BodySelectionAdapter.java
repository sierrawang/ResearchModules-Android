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

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.sagebionetworks.research.mobile_ui.widget.ActionButton;
import org.sagebionetworks.research.presentation.DisplayString;
import org.sagebionetworks.research.presentation.model.form.ChoiceView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BodySelectionAdapter<T> extends RecyclerView.Adapter<BodySelectionViewHolder> {
    private List<ChoiceView<T>> choices;
    private RecyclerView recyclerView;
    private ShowBodySelectionStepFragment fragment;
    private Set<String> selectedChoices;

    public BodySelectionAdapter(final ShowBodySelectionStepFragment fragment, final RecyclerView recyclerView,
                                final List<ChoiceView<T>> choices, HashSet<String> defaultChoices) {
        this.choices = choices;
        this.recyclerView = recyclerView;
        this.fragment = fragment;
        this.selectedChoices = defaultChoices;
    }

    @NonNull
    @Override
    public BodySelectionViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        ActionButton button = (ActionButton) LayoutInflater.from(parent.getContext()).inflate(
                org.sagebionetworks.research.mobile_ui.R.layout.rs2_form_view_holder, parent, false);
        return new BodySelectionViewHolder(this.fragment, this.recyclerView, button);
    }

    // Update the selected choices for this form with the given selection.
    // Return true if the given choice was added to selectedChoices,
    // return false if the given choice was removed.
    public boolean updateSelectedChoices(String selectedChoice) {
        if (selectedChoice.equals("none")) {
            selectedChoices.clear();
        } else {
            selectedChoices.remove("none");
        }
        if (this.selectedChoices.contains(selectedChoice)) {
            selectedChoices.remove(selectedChoice);
            return false;
        } else {
            selectedChoices.add(selectedChoice);
            return true;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final BodySelectionViewHolder holder, final int position) {
        ChoiceView<String> inputField = (ChoiceView<String>)this.choices.get(position);
        String choice = inputField.getAnswerValue();
        holder.setChoice(choice);
        ActionButton button = holder.getButton();
        DisplayString textDisplayString = inputField.getText();
        String text = "";
        if (textDisplayString != null) {
            text = textDisplayString.getDisplayString();
        }

        button.setText(text);
        int color;
        color = button.getContext().getResources().getColor(org.sagebionetworks.research.mobile_ui.R.color.transparent);
        button.setBackgroundColor(color);
    }

    @Override
    public int getItemCount() {
        return this.choices.size();
    }

    public Set<String> getSelectedChoices() {
        return new HashSet<>(this.selectedChoices);
    }
}
