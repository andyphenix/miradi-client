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
package org.miradi.dialogs.planning.upperPanel;

import org.miradi.actions.ActionCollapseAllRows;
import org.miradi.actions.ActionCreateCustomFromCurrentTreeTableConfiguration;
import org.miradi.actions.ActionExpandAllRows;
import org.miradi.actions.ActionPlanningCreationMenu;
import org.miradi.dialogs.planning.MonitoringRowColumnProvider;
import org.miradi.dialogs.planning.treenodes.PlanningTreeRootNode;
import org.miradi.main.MainWindow;
import org.miradi.objects.PlanningTreeRowColumnProvider;

public class MonitoringPlanningTreeTablePanel extends PlanningTreeTablePanel
{
	protected MonitoringPlanningTreeTablePanel(MainWindow mainWindowToUse,
											   PlanningTreeTable treeToUse, 
											   PlanningTreeTableModel modelToUse, 
											   Class[] buttonActions, 
											   PlanningTreeRowColumnProvider rowColumnProvider) throws Exception
	{
		super(mainWindowToUse, treeToUse, modelToUse, buttonActions, rowColumnProvider);
	}

	public static PlanningTreeTablePanel createPlanningTreeTablePanel(MainWindow mainWindowToUse) throws Exception
	{
		PlanningTreeRootNode rootNode = new PlanningTreeRootNode(mainWindowToUse.getProject());
		MonitoringRowColumnProvider rowColumnProvider = new MonitoringRowColumnProvider(mainWindowToUse.getProject());
		PlanningTreeTableModel model = new MonitoringPlanTreeTableModel(mainWindowToUse.getProject(), rootNode, rowColumnProvider);
		PlanningTreeTable treeTable = new PlanningTreeTable(mainWindowToUse, model);
		
		return new MonitoringPlanningTreeTablePanel(mainWindowToUse, treeTable, model, getButtonActions(), rowColumnProvider);
	}
	
	private static Class[] getButtonActions()
	{
		return new Class[] {
				ActionExpandAllRows.class, 
				ActionCollapseAllRows.class, 
				ActionPlanningCreationMenu.class,
				ActionCreateCustomFromCurrentTreeTableConfiguration.class,
		};
	}
}
