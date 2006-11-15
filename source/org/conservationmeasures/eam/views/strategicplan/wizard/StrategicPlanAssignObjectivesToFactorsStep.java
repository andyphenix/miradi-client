/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.views.strategicplan.wizard;

import org.conservationmeasures.eam.views.umbrella.WizardPanel;
import org.conservationmeasures.eam.views.umbrella.WizardStep;

public class StrategicPlanAssignObjectivesToFactorsStep extends WizardStep
{
	//TODO this content no longer belongs to this view
	public StrategicPlanAssignObjectivesToFactorsStep(WizardPanel wizardToUse)
	{
		super(wizardToUse);
	}

	public String getResourceFileName()
	{
		return HTML_FILE_NAME;
	}
	
	private static final String HTML_FILE_NAME = "AssignObjectivesToFactors.html";

}
