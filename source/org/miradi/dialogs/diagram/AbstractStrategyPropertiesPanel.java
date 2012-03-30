/* 
Copyright 2005-2009, Foundations of Success, Bethesda, Maryland 
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

package org.miradi.dialogs.diagram;

import org.miradi.dialogs.base.ObjectDataInputPanelWithSections;
import org.miradi.dialogs.progressReport.ProgressReportSubPanel;
import org.miradi.layout.OneColumnGridLayout;
import org.miradi.main.EAM;
import org.miradi.project.Project;
import org.miradi.schemas.StrategySchema;

abstract public class AbstractStrategyPropertiesPanel extends ObjectDataInputPanelWithSections
{
	public AbstractStrategyPropertiesPanel(Project projectToUse) throws Exception
	{
		super(projectToUse, StrategySchema.getObjectType());
		
		setLayout(new OneColumnGridLayout());
		
		addSubPanelWithTitledBorder(new StrategyCoreSubpanel(getProject(), getMainWindow().getActions(), StrategySchema.getObjectType()));
		addSubPanelWithTitledBorder(new RelatedItemsSubpanel(getProject(), StrategySchema.getObjectType()));
		addSubPanelWithTitledBorder(new FactorSummaryCommentsPanel(getProject(), getMainWindow().getActions(), StrategySchema.getObjectType()));
		addSubPanelWithTitledBorder(new ProgressReportSubPanel(getMainWindow()));
		addBudgetSubPanels();
		
		updateFieldsFromProject();
	}
	
	protected void addBudgetSubPanels() throws Exception
	{
	}
	
	@Override
	public String getPanelDescription()
	{
		return EAM.text("Title|Strategy Properties");
	}
}
