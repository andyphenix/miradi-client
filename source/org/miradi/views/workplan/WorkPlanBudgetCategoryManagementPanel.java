/* 
Copyright 2005-2011, Foundations of Success, Bethesda, Maryland 
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

package org.miradi.views.workplan;

import javax.swing.Icon;

import org.miradi.dialogs.planning.PlanningTreeManagementPanel;
import org.miradi.dialogs.planning.WorkPlanCategoryTreeRowColumnProvider;
import org.miradi.dialogs.planning.propertiesPanel.PlanningTreeMultiPropertiesPanel;
import org.miradi.dialogs.planning.treenodes.PlanningTreeRootNodeAlwaysExpanded;
import org.miradi.dialogs.planning.upperPanel.ExportablePlanningTreeTablePanel;
import org.miradi.dialogs.planning.upperPanel.PlanningTreeTablePanel;
import org.miradi.dialogs.treetables.BudgetCategoryTreeModel;
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ORef;
import org.miradi.objects.PlanningTreeRowColumnProvider;

public class WorkPlanBudgetCategoryManagementPanel extends PlanningTreeManagementPanel
{
	public WorkPlanBudgetCategoryManagementPanel(MainWindow mainWindowToUse,
			PlanningTreeTablePanel planningTreeTablePanelToUse,
			PlanningTreeMultiPropertiesPanel planningTreePropertiesPanel, WorkPlanManagementPanelConfiguration managementConfiguration)
			throws Exception
	{
		super(mainWindowToUse, planningTreeTablePanelToUse, planningTreePropertiesPanel, managementConfiguration.getUniqueTreeTableIdentifier());
		
		mangementConfiguration = managementConfiguration;
	}
	
	@Override
	protected PlanningTreeTablePanel createPlanningTreeTablePanel(String uniqueTreeTableModelIdentifier, PlanningTreeRowColumnProvider rowColumnProvider) throws Exception
	{
		PlanningTreeRootNodeAlwaysExpanded rootNode = new PlanningTreeRootNodeAlwaysExpanded(getMainWindow().getProject());
		BudgetCategoryTreeModel model = new BudgetCategoryTreeModel(getProject(), rootNode, rowColumnProvider, mangementConfiguration.getUniqueTreeTableIdentifier());
	
		return ExportablePlanningTreeTablePanel.createPlanningTreeTablePanelWithoutButtonsForExporting(getMainWindow(), rowColumnProvider, model);
	}
	
	public static WorkPlanBudgetCategoryManagementPanel createManagementPanel(MainWindow mainWindowToUse, WorkPlanManagementPanelConfiguration managementConfiguration) throws Exception
	{
		PlanningTreeRootNodeAlwaysExpanded rootNode = new PlanningTreeRootNodeAlwaysExpanded(mainWindowToUse.getProject());
		WorkPlanCategoryTreeRowColumnProvider rowColumnProvider = managementConfiguration.getRowColumnProvider();
		BudgetCategoryTreeModel model = new BudgetCategoryTreeModel(mainWindowToUse.getProject(), rootNode, rowColumnProvider, managementConfiguration.getUniqueTreeTableIdentifier());
		PlanningTreeTablePanel treeTablePanel = WorkPlanCategoryTreeTablePanel.createPlanningTreeTablePanel(mainWindowToUse, model, rowColumnProvider, managementConfiguration.getButtonActions());
		PlanningTreeMultiPropertiesPanel propertiesPanel = new PlanningTreeMultiPropertiesPanel(mainWindowToUse, ORef.INVALID);
		
		return new WorkPlanBudgetCategoryManagementPanel(mainWindowToUse, treeTablePanel, propertiesPanel, managementConfiguration);
	}

	@Override
	public String getPanelDescription()
	{
		return getMangementConfiguration().getPanelDescription();
	}

	private WorkPlanManagementPanelConfiguration getMangementConfiguration()
	{
		return mangementConfiguration;
	}
	
	@Override
	public Icon getIcon()
	{
		return getMangementConfiguration().getIcon();
	}

	private WorkPlanManagementPanelConfiguration mangementConfiguration;
}
