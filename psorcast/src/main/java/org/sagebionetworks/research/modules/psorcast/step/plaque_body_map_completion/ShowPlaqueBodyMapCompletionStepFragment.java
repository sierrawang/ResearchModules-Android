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

package org.sagebionetworks.research.modules.psorcast.step.plaque_body_map_completion;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.sagebionetworks.research.domain.result.interfaces.Result;
import org.sagebionetworks.research.domain.result.interfaces.TaskResult;
import org.sagebionetworks.research.mobile_ui.show_step.view.ShowStepFragmentBase;
import org.sagebionetworks.research.mobile_ui.show_step.view.ShowUIStepFragmentBase;
import org.sagebionetworks.research.mobile_ui.show_step.view.view_binding.UIStepViewBinding;
import org.sagebionetworks.research.modules.psorcast.R;
import org.sagebionetworks.research.modules.psorcast.result.PlaqueDrawingResult;
import org.sagebionetworks.research.presentation.model.interfaces.StepView;
import org.sagebionetworks.research.presentation.show_step.show_step_view_models.ShowUIStepViewModel;

public class ShowPlaqueBodyMapCompletionStepFragment extends
        ShowUIStepFragmentBase<PlaqueBodyMapCompletionStepView, ShowUIStepViewModel<PlaqueBodyMapCompletionStepView>, UIStepViewBinding<PlaqueBodyMapCompletionStepView>> {

    private BodyRegionImage[] bodyRegions = {new BodyRegionImage("frontUpper", 11933, 236468),
        new BodyRegionImage("frontLower", 7492, 237672),
        new BodyRegionImage("backUpper", 11396, 237699),
        new BodyRegionImage("backLower", 8753, 268900)};

    @NonNull
    public static ShowPlaqueBodyMapCompletionStepFragment newInstance(@NonNull StepView stepView) {
        if (!(stepView instanceof PlaqueBodyMapCompletionStepView)) {
            throw new IllegalArgumentException("Step view: " + stepView + " is not a PlaqueBodyMapCompletionStepView.");
        }

        ShowPlaqueBodyMapCompletionStepFragment fragment = new ShowPlaqueBodyMapCompletionStepFragment();
        Bundle arguments = ShowStepFragmentBase.createArguments(stepView);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.srpm_show_plaque_body_map_completion_step_fragment;
    }

    @NonNull
    @Override
    protected UIStepViewBinding<PlaqueBodyMapCompletionStepView> instantiateAndBindBinding(final View view) {
        return new UIStepViewBinding<>(view);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setPercentages(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpInfoButton(view);
        addResultsToView(view);
    }

    private void setUpInfoButton(View view) {
        Button infoButton = view.findViewById(R.id.info_button);
        infoButton.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        infoButton.setOnClickListener(v -> {
            // TODO - Show the information page
        });
    }

    private void addResultsToView(View view) {
        TaskResult taskResult = this.performTaskViewModel.getTaskResult();
        PlaqueBodyMapCompletionImageView imageView = view.findViewById(R.id.rs2_image_view);
        int i = 0;
        for (BodyRegionImage region : bodyRegions) {
            Result result = taskResult.getResult(region.name);
            if (result instanceof PlaqueDrawingResult) {
                Bitmap bitmap = ((PlaqueDrawingResult) result).getBitmap();
                imageView.addResult(bitmap, i);
            }
            i++;
        }
    }

    private void setPercentages(View view) {
        float coveredPixels = 0;
        float totalPixels = 0;
        TaskResult taskResult = this.performTaskViewModel.getTaskResult();

        for (BodyRegionImage region : bodyRegions) {
            Result result = taskResult.getResult(region.name);
            if (result instanceof PlaqueDrawingResult) {
                // Scale the pixel counts to eliminate screen differences
                float scale = (float)region.totalPixels/((PlaqueDrawingResult) result).getTotalPixels();
                coveredPixels += ((PlaqueDrawingResult) result).getCoveredPixels() * scale;
            }
            totalPixels += region.totalPixels;
            totalPixels -= region.discardPixels;
        }

        double coverage = Math.round((coveredPixels/totalPixels * 100) * 100.0) / 100.0;
        TextView title = view.findViewById(R.id.title);
        title.setText(coverage + "% Plaque Coverage");
        TextView text = view.findViewById(R.id.text);
        text.setText("You have noted " + coverage +  "% of your skin is affected by psoriasis.");
    }
}

class BodyRegionImage {
    public String name;
    public int discardPixels;
    public int totalPixels;
    public BodyRegionImage(String name, int discardPixels, int totalPixels) {
        this.name = name;
        this.discardPixels = discardPixels;
        this.totalPixels = totalPixels;
    }
}