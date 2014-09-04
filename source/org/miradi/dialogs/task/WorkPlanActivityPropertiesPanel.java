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

package org.miradi.dialogs.task;

import org.miradi.dialogs.activity.ActivityFactorVisibilityControlPanel;
import org.miradi.dialogs.base.ObjectDataInputPanel;
import org.miradi.main.MainWindow;

public class WorkPlanActivityPropertiesPanel extends TaskPropertiesPanel
{
	public WorkPlanActivityPropertiesPanel(MainWindow mainWindow) throws Exception
	{
		super(mainWindow);
	}
	
	@Override
	protected ObjectDataInputPanel createDetailsPanel(MainWindow mainWindow, ActivityFactorVisibilityControlPanel activityVisibilityButtonPanel) throws Exception
	{
		return new ActivityDetailsPanelWithRelevancyOverrideFields(getProject(), mainWindow.getActions(), activityVisibilityButtonPanel);
	}

	@Override
	protected boolean shouldHaveVisibilityPanel()
	{
		return false;
	}

	@Override
	public String getPanelDescription()
	{
		return "WorkPlanActivityPropertiesPanel";
	}
}
