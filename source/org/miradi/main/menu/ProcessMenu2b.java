/* 
Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
(on behalf of the Conservation Measures Partnership, "CMP") and 
Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 

This file is part of Miradi

Miradi is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License version 3, 
as published by the Free Software Foundation.

Miradi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Miradi.  If not, see <http://www.gnu.org/licenses/>. 
*/ 
package org.miradi.main.menu;

import java.awt.event.KeyEvent;

import org.miradi.actions.Actions;
import org.miradi.actions.jump.ActionJumpMonitoringWizardDefineIndicatorsStep;
import org.miradi.actions.jump.ActionJumpDiagramWizardDefineAudienceStep;
import org.miradi.actions.jump.ActionJumpPlanningWizardFinalizeMonitoringPlanStep;

public class ProcessMenu2b extends MiradiMenu
{
	public ProcessMenu2b(Actions actions)
	{
		super(ProcessSteps.PROCESS_STEP_2B, actions);
		setMnemonic(KeyEvent.VK_S);
		
		addMenuItem(ActionJumpDiagramWizardDefineAudienceStep.class, KeyEvent.VK_M);
		//addMenuItem(ActionJumpDefineAudiences.class, KeyEvent.VK_A);
		addMenuItem(ActionJumpMonitoringWizardDefineIndicatorsStep.class, KeyEvent.VK_I);
		//addMenuItem(ActionJumpPlanDataStorage.class, KeyEvent.VK_D);
		addMenuItem(ActionJumpPlanningWizardFinalizeMonitoringPlanStep.class, KeyEvent.VK_F);
	}
}
