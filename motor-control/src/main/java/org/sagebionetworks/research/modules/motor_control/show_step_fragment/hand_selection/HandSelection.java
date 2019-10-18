package org.sagebionetworks.research.modules.motor_control.show_step_fragment.hand_selection;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({HandSelection.LEFT, HandSelection.RIGHT, HandSelection.BOTH})
public @interface HandSelection {
    String LEFT = "left";
    String RIGHT = "right";
    String BOTH = "both";
}
