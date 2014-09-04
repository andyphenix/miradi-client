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
package org.miradi.dialogs.planning.upperPanel;

import org.miradi.actions.ActionCollapseAllRows;
import org.miradi.actions.ActionDeletePlanningViewTreeNode;
import org.miradi.actions.ActionExpandAllRows;
import org.miradi.actions.ActionFilterWorkPlanByProjectResource;
import org.miradi.actions.ActionPlanningCreationMenu;
import org.miradi.actions.ActionTreeNodeDown;
import org.miradi.actions.ActionTreeNodeUp;
import org.miradi.actions.ActionWorkPlanBudgetCustomizeTableEditor;
import org.miradi.dialogs.planning.WorkPlanRowColumnProvider;
import org.miradi.dialogs.planning.propertiesPanel.AbstractFixedHeightDirectlyAboveTreeTablePanel;
import org.miradi.dialogs.planning.treenodes.PlanningTreeRootNodeAlwaysExpanded;
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ORefSet;
import org.miradi.objects.PlanningTreeRowColumnProvider;
import org.miradi.objects.TableSettings;

import com.jhlabs.awt.GridLayoutPlus;

public class WorkPlanTreeTablePanel extends PlanningTreeTablePanel
{
	protected WorkPlanTreeTablePanel(MainWindow mainWindowToUse,
									 PlanningTreeTable treeToUse,
									 PlanningTreeTableModel modelToUse,
									 Class[] buttonActions,
									 PlanningTreeRowColumnProvider rowColumnProvider, 
									 AbstractFixedHeightDirectlyAboveTreeTablePanel filterStatusPanelToUse) throws Exception
	{
		super(mainWindowToUse, treeToUse, modelToUse, buttonActions, rowColumnProvider, filterStatusPanelToUse);
	}

	public static PlanningTreeTablePanel createPlanningTreeTablePanel(MainWindow mainWindowToUse) throws Exception
	{
		WorkPlanRowColumnProvider rowColumnProvider = new WorkPlanRowColumnProvider(mainWindowToUse.getProject());
		PlanningTreeRootNodeAlwaysExpanded rootNode = new PlanningTreeRootNodeAlwaysExpanded(mainWindowToUse.getProject());
		PlanningTreeTableModel model = new WorkPlanTreeTableModel(mainWindowToUse.getProject(), rootNode, rowColumnProvider);
		PlanningTreeTable treeTable = new PlanningTreeTableWithVisibleRootNode(mainWindowToUse, model);
		AbstractFixedHeightDirectlyAboveTreeTablePanel filterStatusPanel = new AbstractFixedHeightDirectlyAboveTreeTablePanel();

		return new WorkPlanTreeTablePanel(mainWindowToUse, treeTable, model, getButtonActions(), rowColumnProvider, filterStatusPanel);
	}
	
	@Override
	protected void updateResourceFilter() throws Exception
	{
		TableSettings tableSettings = TableSettings.findOrCreate(getProject(), getTabSpecificModelIdentifier());
		ORefList projectResourceFilterRefs = tableSettings.getTableSettingsMap().getRefList(TableSettings.WORK_PLAN_PROJECT_RESOURCE_FILTER_CODELIST_KEY);
		ORefSet projectResourceRefsToRetain = new ORefSet(projectResourceFilterRefs);
		
		getWorkUnitsTableModel().setResourcesFilter(projectResourceRefsToRetain);
		getBudgetDetailsTableModel().setResourcesFilter(projectResourceRefsToRetain);
		getPlanningViewMainTableModel().setResourcesFilter(projectResourceRefsToRetain);

		getMainWindow().updatePlanningDateRelatedStatus();
	}
		
	public static String getTabSpecificModelIdentifier()
	{
		final String TAB_TAG = "Tab_Tag";
		return WorkPlanTreeTableModel.UNIQUE_TREE_TABLE_IDENTIFIER + TAB_TAG;
	}
	
	@Override
	protected GridLayoutPlus createButtonLayout()
	{
		return new GridLayoutPlus(2, 4, 1, 1);
	}

	private static Class[] getButtonActions()
	{
		return new Class[] {
				ActionExpandAllRows.class, 
				ActionTreeNodeUp.class,
				ActionPlanningCreationMenu.class,
				ActionWorkPlanBudgetCustomizeTableEditor.class,
				
				ActionCollapseAllRows.class,
				ActionTreeNodeDown.class,
				ActionDeletePlanningViewTreeNode.class,
				ActionFilterWorkPlanByProjectResource.class,
		};
	}
}
