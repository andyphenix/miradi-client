/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.targetviability.wizard;

import org.conservationmeasures.eam.actions.views.ActionViewTargetViability;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.wizard.TargetViabilityWizardStep;
import org.conservationmeasures.eam.wizard.WizardPanel;

public class TargetViabilityOverviewStep extends TargetViabilityWizardStep
{
	public TargetViabilityOverviewStep(WizardPanel wizardToUse)
	{
		super(wizardToUse);
	}

	public String getProcessStepTitle()
	{
		return "";
	}

	public Class getAssociatedActionClass()
	{
		return ActionViewTargetViability.class;
	}
	
	public String getSubHeading()
	{
		return EAM.text("Page 1");
	}
}
