/*
 * Copyright 2015 Apple Inc.
 * Ported to Android from ResearchKit/ResearchKit 1.5
 */

package org.sagebionetworks.research.modules.motor_control.show_step_fragment.tapping;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({TappingButtonIdentifier.LEFT, TappingButtonIdentifier.RIGHT, TappingButtonIdentifier.NONE})
public @interface TappingButtonIdentifier {
    String LEFT = "left";
    String RIGHT = "right";
    String NONE = "none";
}