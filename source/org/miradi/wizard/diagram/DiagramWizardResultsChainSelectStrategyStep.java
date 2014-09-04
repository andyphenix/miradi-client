/* 
Copyright 2005-2014, Foundations of Success, Bethesda, Maryland
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
package org.miradi.wizard.diagram;

import org.miradi.actions.jump.ActionJumpDiagramWizardResultsChainSelectStrategyStep;
import org.miradi.main.EAM;
import org.miradi.main.menu.ProcessSteps;
import org.miradi.wizard.DiagramWizardStep;
import org.miradi.wizard.WizardPanel;

public class DiagramWizardResultsChainSelectStrategyStep extends DiagramWizardStep
{
	public DiagramWizardResultsChainSelectStrategyStep(WizardPanel panelToUse)
	{
		super(panelToUse);
	}
	
	@Override
	public String getProcessStepTitle()
	{
		return ProcessSteps.PROCESS_STEP_2A;
	}

	@Override
	public Class getAssociatedActionClass()
	{
		return ActionJumpDiagramWizardResultsChainSelectStrategyStep.class;
	}
	
	@Override
	public String getSubHeading()
	{
		return EAM.text("1) Select strategy to focus on ");
	}
}
