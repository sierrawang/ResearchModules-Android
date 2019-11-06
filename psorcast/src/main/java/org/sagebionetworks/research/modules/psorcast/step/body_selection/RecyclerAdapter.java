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

import static org.sagebionetworks.research.modules.psorcast.R.*;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.sagebionetworks.research.mobile_ui.widget.ActionButton;
import org.sagebionetworks.research.modules.psorcast.R;
import org.sagebionetworks.research.presentation.DisplayString;
import org.sagebionetworks.research.presentation.mapper.DrawableMapper;
import org.sagebionetworks.research.presentation.model.form.ChoiceView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerAdapter.ImageViewHolder> {

    private List<ChoiceView<T>> choices;
    private RecyclerView recyclerView;
    private ShowBodySelectionStepFragment fragment;
    private Set<String> selectedChoices;

    public RecyclerAdapter(final ShowBodySelectionStepFragment fragment, final RecyclerView recyclerView,
            final List<ChoiceView<T>> choices, HashSet<String> defaultChoices) {
        this.choices = choices;
        this.recyclerView = recyclerView;
        this.fragment = fragment;
        this.selectedChoices = defaultChoices;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        ActionButton button = (ActionButton) LayoutInflater.from(parent.getContext()).inflate(layout.srpm_image_selection, parent, false);
        return new ImageViewHolder(button, this.recyclerView, this.fragment);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, final int position) {
        ChoiceView<String> inputField = (ChoiceView<String>)this.choices.get(position);
        String choice = inputField.getAnswerValue();
        holder.setChoice(choice);
        ActionButton button = holder.getButton();
        DisplayString imageDisplayString = inputField.getText();
        String image = "";
        if (imageDisplayString != null) {
            image = imageDisplayString.getDisplayString();
        }
        int id = button.getContext().getResources().getIdentifier(image, "drawable", button.getContext().getPackageName());
        button.setBackground(button.getContext().getResources().getDrawable(id));
    }

    @Override
    public int getItemCount() {
        return this.choices.size();
    }

    public boolean updateSelectedChoices(String choice) {
        if (this.selectedChoices.contains(choice)) {
            selectedChoices.remove(choice);
            return false;
        } else {
            selectedChoices.add(choice);
            return true;
        }
    }

    public HashSet<String> getSelectedChoices() {
        return new HashSet<>(this.selectedChoices);
    }


    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ActionButton button;
        String choice;
        RecyclerAdapter adapter;
        ShowBodySelectionStepFragment fragment;

        public ImageViewHolder(ActionButton button, RecyclerView recyclerView, ShowBodySelectionStepFragment fragment) {
            super(button);
            this.button = button;
            this.adapter = (RecyclerAdapter) recyclerView.getAdapter();
            this.fragment = fragment;

            this.button.setOnClickListener(view -> {
                adapter.updateSelectedChoices(this.choice);
                fragment.writeBodySelectionResult(adapter.getSelectedChoices());
            });
        }

        public ActionButton getButton() {
            return this.button;
        }

        public void setChoice(@NonNull String choice) {
            this.choice = choice;
        }
    }
}
