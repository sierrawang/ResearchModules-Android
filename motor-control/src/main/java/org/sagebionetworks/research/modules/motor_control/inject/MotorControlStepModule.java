package org.sagebionetworks.research.modules.motor_control.inject;

import com.google.gson.TypeAdapterFactory;

import org.sagebionetworks.research.modules.common.step.completion.CompletionStepModule;
import org.sagebionetworks.research.modules.common.step.instruction.InstructionStepModule;
import org.sagebionetworks.research.modules.common.step.overview.OverviewStepModule;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;

@Module(includes = {HandNavigationInstructionStepModule.class, CompletionStepModule.class, HandSelectionStepModule.class, MPowerActiveStepModule.class,
        OverviewStepModule.class, TappingCompletionStepModule.class, TappingStepModule.class, MtcCountdownStepModule.class})
public class MotorControlStepModule {
    @Provides
    @IntoSet
    static TypeAdapterFactory provideMotorControlAutoValueTypeAdapterFactory() {
        return MotorControlAutoValueTypeAdapterFactory.create();
    }
}
