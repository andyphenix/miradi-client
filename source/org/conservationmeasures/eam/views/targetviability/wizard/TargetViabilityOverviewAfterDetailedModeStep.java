package org.conservationmeasures.eam.views.targetviability.wizard;

import org.conservationmeasures.eam.actions.jump.ActionJumpTargetViabilityMethodChoiceStep;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.menu.ProcessSteps;
import org.conservationmeasures.eam.wizard.TargetViabilityWizardStep;
import org.conservationmeasures.eam.wizard.WizardPanel;

public class TargetViabilityOverviewAfterDetailedModeStep extends TargetViabilityWizardStep
{
	public TargetViabilityOverviewAfterDetailedModeStep(WizardPanel wizardToUse)
	{
		super(wizardToUse);
	}

	public String getResourceFileName()
	{
		return HTML_FILENAME;
	}
	
	public String getHtmlBaseName()
	{
		return getResourceFileName();
	}

	
	public String getProcessStepTitle()
	{
		return ProcessSteps.PROCESS_STEP_1B;
	}

	public Class getAssociatedActionClass()
	{
		return ActionJumpTargetViabilityMethodChoiceStep.class;
	}
	
	public String getSubHeading()
	{
		return EAM.text("Page 8");
	}

	String HTML_FILENAME = "TargetViabilityOverviewStep";
}
