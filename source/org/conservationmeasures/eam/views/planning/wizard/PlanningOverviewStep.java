/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 

package org.conservationmeasures.eam.views.planning.wizard;

import org.conservationmeasures.eam.actions.views.ActionViewPlanning;
import org.conservationmeasures.eam.views.planning.PlanningView;
import org.conservationmeasures.eam.wizard.SplitWizardStep;
import org.conservationmeasures.eam.wizard.WizardPanel;

public class PlanningOverviewStep extends SplitWizardStep
{
	public PlanningOverviewStep(WizardPanel wizardToUse)
	{
		super(wizardToUse, PlanningView.getViewName());
	}

	public String getProcessStepTitle()
	{
		return "";
	}

	public Class getAssociatedActionClass()
	{
		return ActionViewPlanning.class;
	}
	
}
