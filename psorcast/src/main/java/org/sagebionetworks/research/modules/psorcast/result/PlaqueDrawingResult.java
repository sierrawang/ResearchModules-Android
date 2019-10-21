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

package org.sagebionetworks.research.modules.psorcast.result;

import android.graphics.Bitmap;
import android.graphics.Path;
import androidx.annotation.NonNull;

import com.google.auto.value.AutoValue;

import org.sagebionetworks.research.domain.result.interfaces.Result;
import org.threeten.bp.Instant;

import java.util.ArrayList;


@AutoValue
public abstract class PlaqueDrawingResult implements Result {
    @AutoValue.Builder
    public abstract static class Builder {
        public abstract PlaqueDrawingResult build();

        public abstract Builder setIdentifier(@NonNull String identifier);
        public abstract Builder setStartTime(@NonNull Instant endTime);
        public abstract Builder setEndTime(@NonNull Instant endTime);

        public abstract Builder setPaths(@NonNull ArrayList<Path> drawings);
        public abstract Builder setBitmap(@NonNull Bitmap frontBitmap);
    }

    public static Builder builder() {
        return new AutoValue_PlaqueDrawingResult.Builder();
    }

    @Override
    @NonNull
    public String getType() {
        return "plaqueDrawing";
    }

    @NonNull
    public abstract ArrayList<Path> getPaths();

    @NonNull
    public abstract Bitmap getBitmap();
}
