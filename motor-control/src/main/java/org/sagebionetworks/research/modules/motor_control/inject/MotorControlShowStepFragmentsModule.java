package org.sagebionetworks.research.modules.motor_control.inject;

import org.sagebionetworks.research.mobile_ui.inject.ShowStepFragmentScope;
import org.sagebionetworks.research.modules.motor_control.show_step_fragment.MtcCountdownStepFragment;
import org.sagebionetworks.research.modules.common.step.completion.ShowCompletionStepFragment;
import org.sagebionetworks.research.modules.motor_control.show_step_fragment.ShowHandNavigationInstructionStepFragment;
import org.sagebionetworks.research.modules.motor_control.show_step_fragment.ShowMPowerActiveUIStepFragment;
import org.sagebionetworks.research.modules.common.step.overview.ShowOverviewStepFragment;
import org.sagebionetworks.research.modules.motor_control.show_step_fragment.ShowTappingCompletionStepFragment;
import org.sagebionetworks.research.modules.motor_control.show_step_fragment.hand_selection.ShowHandSelectionStepFragment;
import org.sagebionetworks.research.modules.motor_control.show_step_fragment.tapping.ShowTappingStepFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class MotorControlShowStepFragmentsModule {
    @ContributesAndroidInjector
    @ShowStepFragmentScope
    abstract ShowHandSelectionStepFragment contributeShowHandSelectionStepFragmentInjector();

    @ContributesAndroidInjector
    @ShowStepFragmentScope
    abstract ShowHandNavigationInstructionStepFragment contributeShowHandNavigationInstructionStepFragment();

    @ContributesAndroidInjector
    @ShowStepFragmentScope
    abstract ShowMPowerActiveUIStepFragment contributeShowMPowerActiveUIStepFragmentInjector();

    @ContributesAndroidInjector
    @ShowStepFragmentScope
    abstract ShowOverviewStepFragment contributeShowOverviewStepFragmentInjector();

    @ContributesAndroidInjector
    @ShowStepFragmentScope
    abstract ShowTappingCompletionStepFragment contributeShowTappingCompletionStepFragmentInjector();

    @ContributesAndroidInjector
    @ShowStepFragmentScope
    abstract ShowTappingStepFragment contributeShowTappingStepFragmentInjector();

    @ContributesAndroidInjector
    @ShowStepFragmentScope
    abstract ShowCompletionStepFragment contributeShowCompletionStepFragmentInjector();

    @ContributesAndroidInjector
    @ShowStepFragmentScope
    abstract MtcCountdownStepFragment contributeShowMtcCountdownStepFragmentInjector();
}
