/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.main.menu;

import java.awt.event.KeyEvent;

import org.conservationmeasures.eam.actions.Actions;
import org.conservationmeasures.eam.main.EAM;

public class ProcessMenu4a extends MiradiMenu
{
	public ProcessMenu4a(Actions actions)
	{
		super(ProcessSteps.PROCESS_STEP_4A, actions);
		setMnemonic(KeyEvent.VK_D);

		addDisabledMenuItem(EAM.text("Develop systems for recording, storing, processing and backing up project data"));
//		addMenuItem(ActionJumpReviewStratAndMonPlansStep.class, KeyEvent.VK_R);
//		addMenuItem(ActionJumpWorkPlanAssignResourcesStep.class, KeyEvent.VK_T);
//		addMenuItem(ActionJumpScheduleOverviewStep.class, KeyEvent.VK_S);
//		addMenuItem(ActionJumpFinancialOverviewStep.class, KeyEvent.VK_F);
	}
}
