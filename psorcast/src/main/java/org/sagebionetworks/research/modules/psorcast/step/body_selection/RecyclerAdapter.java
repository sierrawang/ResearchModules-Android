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

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Build.VERSION_CODES;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.sagebionetworks.research.modules.psorcast.R;
import org.sagebionetworks.research.presentation.DisplayString;
import org.sagebionetworks.research.presentation.model.form.ChoiceView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerAdapter.SelectionViewHolder> {

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

    @RequiresApi(api = VERSION_CODES.LOLLIPOP)
    @NonNull
    @Override
    public SelectionViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        ConstraintLayout button = (ConstraintLayout) LayoutInflater.from(parent.getContext()).inflate(layout.srpm_body_selection_button, parent, false);
        return new SelectionViewHolder(button, this.recyclerView, this.fragment);
    }

    @Override
    public void onBindViewHolder(SelectionViewHolder holder, final int position) {
        ChoiceView<String> inputField = (ChoiceView<String>)this.choices.get(position);
        String choice = inputField.getAnswerValue();
        holder.setChoice(choice);
        ConstraintLayout button = holder.getButton();
        DisplayString imageDisplayString = inputField.getText();
        String image = "";
        if (imageDisplayString != null) {
            image = imageDisplayString.getDisplayString();
        }
        int id = button.getContext().getResources().getIdentifier(image, "drawable", button.getContext().getPackageName());
        ImageView buttonImage = button.findViewById(R.id.button_image);
        buttonImage.setImageResource(id);
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


    public static class SelectionViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout button;
        String choice;
        RecyclerAdapter adapter;
        ShowBodySelectionStepFragment fragment;
        int selectedColor;
        int unselectedColor;
        ImageView checkCircle;

        @RequiresApi(api = VERSION_CODES.LOLLIPOP)
        public SelectionViewHolder(ConstraintLayout button, RecyclerView recyclerView, ShowBodySelectionStepFragment fragment) {
            super(button);
            this.button = button;
            this.adapter = (RecyclerAdapter) recyclerView.getAdapter();
            this.fragment = fragment;
            Resources resources = recyclerView.getContext().getResources();
            this.selectedColor = resources.getColor(color.colorPrimary);
            this.unselectedColor = resources.getColor(color.colorSecondary);
            this.checkCircle = this.button.findViewById(id.check_circle);

            this.button.setOnClickListener(view -> {
                if (adapter.updateSelectedChoices(this.choice)) {
                    this.button.setBackgroundTintList(ColorStateList.valueOf(this.selectedColor));
                    checkCircle.setImageResource(drawable.srpm_check_circle_checked);
                } else {
                    this.button.setBackgroundTintList(ColorStateList.valueOf(this.unselectedColor));
                    checkCircle.setImageResource(drawable.srpm_check_circle_unchecked);
                }
                fragment.writeBodySelectionResult(adapter.getSelectedChoices());
            });
        }

        public ConstraintLayout getButton() {
            return this.button;
        }

        public void setChoice(@NonNull String choice) {
            this.choice = choice;
        }
    }
}
