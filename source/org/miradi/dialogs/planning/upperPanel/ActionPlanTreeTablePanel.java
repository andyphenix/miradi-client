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
import org.miradi.dialogs.planning.ActionPlanMultiRowColumnProvider;
import org.miradi.dialogs.planning.PlanningViewActionButtonPanel;
import org.miradi.dialogs.planning.treenodes.PlanningTreeRootNodeAlwaysExpanded;
import org.miradi.main.MainWindow;
import org.miradi.objects.PlanningTreeRowColumnProvider;

public class ActionPlanTreeTablePanel extends PlanningTreeTablePanel
{
	protected ActionPlanTreeTablePanel(MainWindow mainWindowToUse,
			   							PlanningTreeTable treeToUse, 
			   							PlanningTreeTableModel modelToUse, 
			   							Class[] buttonActions, 
			   							PlanningTreeRowColumnProvider rowColumnProvider) throws Exception
	{
		super(mainWindowToUse, treeToUse, modelToUse, buttonActions, rowColumnProvider);
		
		choicePanel = new PlanningViewActionButtonPanel(getProject());
		addComponentAsFirst(choicePanel);
	}

	public static PlanningTreeTablePanel createPlanningTreeTablePanel(MainWindow mainWindowToUse) throws Exception
	{
		ActionPlanMultiRowColumnProvider rowColumnProvider = new ActionPlanMultiRowColumnProvider(mainWindowToUse.getProject());
		PlanningTreeRootNodeAlwaysExpanded rootNode = new PlanningTreeRootNodeAlwaysExpanded(mainWindowToUse.getProject());
		PlanningTreeTableModel model = new ActionPlanTreeTableModel(mainWindowToUse.getProject(), rootNode, rowColumnProvider);
		PlanningTreeTable treeTable = new PlanningTreeTable(mainWindowToUse, model);
		
		return new ActionPlanTreeTablePanel(mainWindowToUse, treeTable, model, getButtonActions(), rowColumnProvider);
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
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		choicePanel.dispose();
	}
	
	private PlanningViewActionButtonPanel choicePanel;
}
