/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.threatmatrix.wizard;

import org.conservationmeasures.eam.actions.jump.ActionJumpThreatMatrixOverviewStep;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.menu.ProcessSteps;
import org.conservationmeasures.eam.wizard.WizardPanel;


public class ThreatRatingWizardSeverityStep extends ThreatRatingWizardSetValue
{
	public ThreatRatingWizardSeverityStep(WizardPanel wizardToUse) throws Exception
	{
		super(wizardToUse, "Severity");
	}
	
	public ThreatRatingWizardSeverityStep(WizardPanel wizardToUse,  String critertion) throws Exception
	{
		super(wizardToUse, critertion);
	}
	
	public String getProcessStepTitle()
	{
		return ProcessSteps.PROCESS_STEP_1C;
	}

	public Class getAssociatedActionClass()
	{
		return ActionJumpThreatMatrixOverviewStep.class;
	}
	
	public String getSubHeading()
	{
		return EAM.text("Page 3");
	}
}
