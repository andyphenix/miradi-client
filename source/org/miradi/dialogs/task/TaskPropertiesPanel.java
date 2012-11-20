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
package org.miradi.dialogs.task;

import org.miradi.dialogs.activity.ActivityFactorVisibilityControlPanel;
import org.miradi.dialogs.assignment.AssignmentsPropertiesPanel;
import org.miradi.dialogs.base.ObjectDataInputPanel;
import org.miradi.dialogs.base.ObjectDataInputPanelWithSections;
import org.miradi.dialogs.diagram.WorkPlanPanelPropertiesPanel;
import org.miradi.dialogs.expense.ExpensesPropertiesPanel;
import org.miradi.dialogs.progressReport.ProgressReportSubPanel;
import org.miradi.main.CommandExecutedEvent;
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ORef;
import org.miradi.objects.Target;
import org.miradi.schemas.TargetSchema;
import org.miradi.schemas.TaskSchema;

public abstract class TaskPropertiesPanel extends ObjectDataInputPanelWithSections
{
	protected TaskPropertiesPanel(MainWindow mainWindow) throws Exception
	{
		super(mainWindow.getProject(), TaskSchema.getObjectType());
		
		addSubPanelWithTitledBorder(createDetailsPanel(mainWindow));

		if(shouldHaveVisibilityPanel())
			addSubPanelWithTitledBorder(new ActivityFactorVisibilityControlPanel(mainWindow));
				
		addSubPanelWithTitledBorder(new ProgressReportSubPanel(getMainWindow()));
		addSubPanelWithTitledBorder(new WorkPlanPanelPropertiesPanel(getProject(), ORef.createInvalidWithType(TaskSchema.getObjectType())));
		addBudgetSubPanels();
		
		updateFieldsFromProject();
	}

	protected ObjectDataInputPanel createDetailsPanel(MainWindow mainWindow) throws Exception
	{
		return new TaskDetailsPanel(getProject(), mainWindow.getActions());
	}

	protected void addBudgetSubPanels() throws Exception
	{
		addSubPanelWithTitledBorder(new AssignmentsPropertiesPanel(getMainWindow(), TaskSchema.getObjectType(), getPicker()));
		addSubPanelWithTitledBorder(new ExpensesPropertiesPanel(getMainWindow(), TaskSchema.getObjectType(), getPicker()));
	}

	@Override
	public void commandExecuted(CommandExecutedEvent event)
	{
		super.commandExecuted(event);
			
		if (event.isSetDataCommandWithThisTypeAndTag(TargetSchema.getObjectType(), Target.TAG_VIABILITY_MODE))
			reloadSelectedRefs();
	}
	
	abstract protected boolean shouldHaveVisibilityPanel();
}
