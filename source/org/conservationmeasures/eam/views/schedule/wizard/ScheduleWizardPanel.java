/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.schedule.wizard;

import org.conservationmeasures.eam.actions.Actions;
import org.conservationmeasures.eam.actions.jump.ActionJumpBudgetFutureDemo;
import org.conservationmeasures.eam.actions.jump.ActionJumpDevelopSchedule;
import org.conservationmeasures.eam.main.MainWindow;
import org.conservationmeasures.eam.views.umbrella.WizardPanel;

public class ScheduleWizardPanel extends WizardPanel
{
	public ScheduleWizardPanel(MainWindow mainWindow) throws Exception
	{
		super(mainWindow);
		actions  = mainWindow.getActions();

		WELCOME = addStep(new ScheduleWizardWelcomeStep(this));
		setStep(WELCOME);
	}

	public void jump(Class stepMarker) throws Exception
	{
		if (stepMarker.equals(ActionJumpDevelopSchedule.class))
			setStep(WELCOME);
		else
			throw new RuntimeException("Step not in this view: " + stepMarker);
	}

	public void previous() throws Exception
	{
		if (currentStep == WELCOME)
			actions.get(ActionJumpBudgetFutureDemo.class).doAction();
		else
			super.previous();

	}
	
	private int WELCOME;
	Actions actions;
}
