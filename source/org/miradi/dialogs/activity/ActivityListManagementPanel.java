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
package org.miradi.dialogs.activity;

import javax.swing.Icon;

import org.miradi.actions.jump.ActionJumpEditAllStrategiesStep;
import org.miradi.dialogs.base.ObjectListManagementPanel;
import org.miradi.dialogs.task.ActivityPropertiesPanelWithoutBudgetPanels;
import org.miradi.icons.ActivityIcon;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ORefList;

public class ActivityListManagementPanel extends ObjectListManagementPanel
{
	public static ActivityListManagementPanel create(MainWindow mainWindow, ORefList selectedStrategyHierarchy) throws Exception
	{
		ActivityListTablePanel tablePanel = new ActivityListTablePanel(mainWindow, selectedStrategyHierarchy);
		ActivityPropertiesPanelWithoutBudgetPanels properties = new ActivityPropertiesPanelWithoutBudgetPanels(mainWindow);
		return new ActivityListManagementPanel(mainWindow, tablePanel, properties);
	}
	
	private ActivityListManagementPanel(MainWindow mainWindow, ActivityListTablePanel tablePanel, ActivityPropertiesPanelWithoutBudgetPanels properties) throws Exception
	{
		super(mainWindow, tablePanel, properties);
	}
	
	@Override
	public String getSplitterDescription()
	{
		return getPanelDescription() + SPLITTER_TAG;
	}
	
	@Override
	public String getPanelDescription()
	{
		return PANEL_DESCRIPTION;
	}
	
	@Override
	public Icon getIcon()
	{
		return new ActivityIcon();
	}
	
	@Override
	public Class getJumpActionClass()
	{
		return ActionJumpEditAllStrategiesStep.class;
	}
	
	private static String PANEL_DESCRIPTION = EAM.text("Tab|Activities"); 
}
