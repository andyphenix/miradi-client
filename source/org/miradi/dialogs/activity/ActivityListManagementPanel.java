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
package org.miradi.dialogs.activity;

import javax.swing.Icon;

import org.miradi.actions.jump.ActionJumpEditAllStrategiesStep;
import org.miradi.dialogs.base.ObjectListManagementPanel;
import org.miradi.dialogs.task.TaskPropertiesPanel;
import org.miradi.icons.ActivityIcon;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ORef;

public class ActivityListManagementPanel extends ObjectListManagementPanel
{
	public ActivityListManagementPanel(MainWindow mainWindow, ORef nodeRef) throws Exception
	{
		super(mainWindow, new ActivityListTablePanel(mainWindow.getProject(), mainWindow.getActions(), nodeRef),
				TaskPropertiesPanel.createWithVisibilityPanel(mainWindow));
	}
	
	public String getSplitterDescription()
	{
		return getPanelDescription() + SPLITTER_TAG;
	}
	
	public String getPanelDescription()
	{
		return PANEL_DESCRIPTION;
	}
	
	public Icon getIcon()
	{
		return new ActivityIcon();
	}
	
	public Class getJumpActionClass()
	{
		return ActionJumpEditAllStrategiesStep.class;
	}
	
	private static String PANEL_DESCRIPTION = EAM.text("Tab|Activities"); 
}
