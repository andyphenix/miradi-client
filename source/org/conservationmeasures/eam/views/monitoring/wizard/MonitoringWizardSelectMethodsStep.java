/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.monitoring.wizard;

import org.conservationmeasures.eam.actions.jump.ActionJumpMonitoringWizardSelectMethodsStep;
import org.conservationmeasures.eam.main.menu.ProcessSteps;
import org.conservationmeasures.eam.wizard.WizardPanel;
import org.conservationmeasures.eam.wizard.WorkPlanWizardStep;

public class MonitoringWizardSelectMethodsStep extends WorkPlanWizardStep
{
	public MonitoringWizardSelectMethodsStep(WizardPanel wizardToUse)
	{
		super(wizardToUse);
	}
	
	public String getProcessStepTitle()
	{
		return ProcessSteps.PROCESS_STEP_4A;
	}

	public Class getAssociatedActionClass()
	{
		return ActionJumpMonitoringWizardSelectMethodsStep.class;
	}

}