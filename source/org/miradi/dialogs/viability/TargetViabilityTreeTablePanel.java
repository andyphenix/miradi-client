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

package org.miradi.dialogs.viability;

import org.miradi.actions.ActionCreateKeyEcologicalAttribute;
import org.miradi.actions.ActionCreateKeyEcologicalAttributeIndicator;
import org.miradi.actions.ActionCreateKeyEcologicalAttributeMeasurement;
import org.miradi.actions.ActionDeleteKeyEcologicalAttribute;
import org.miradi.actions.ActionDeletePlanningViewTreeNode;
import org.miradi.actions.ActionExpandToMenu;
import org.miradi.dialogs.planning.propertiesPanel.PlanningViewAbstractTreeTableSyncedTableModel;
import org.miradi.dialogs.planning.treenodes.PlanningTreeRootNodeAlwaysExpanded;
import org.miradi.dialogs.planning.upperPanel.PlanningTreeMultiTableModel;
import org.miradi.dialogs.planning.upperPanel.PlanningTreeTable;
import org.miradi.dialogs.planning.upperPanel.PlanningTreeTablePanel;
import org.miradi.dialogs.planning.upperPanel.PlanningUpperMultiTable;
import org.miradi.dialogs.planning.upperPanel.TreeTableModelWithRebuilder;
import org.miradi.dialogs.planning.upperPanel.ViabilityTreeTable;
import org.miradi.dialogs.planning.upperPanel.ViabilityTreeTableModel;
import org.miradi.dialogs.planning.upperPanel.ViabilityTreeTableWithVisibleRootNode;
import org.miradi.dialogs.planning.upperPanel.ViabilityUpperMultiTable;
import org.miradi.dialogs.planning.upperPanel.ViabilityViewMainTableModel;
import org.miradi.dialogs.treetables.GenericTreeTableModel;
import org.miradi.dialogs.treetables.TreeTableNode;
import org.miradi.main.MainWindow;
import org.miradi.objects.PlanningTreeRowColumnProvider;
import org.miradi.project.Project;


public class TargetViabilityTreeTablePanel extends PlanningTreeTablePanel
{
	protected TargetViabilityTreeTablePanel(MainWindow mainWindowToUse, PlanningTreeTable treeToUse, GenericTreeTableModel modelToUse,
											Class[] buttonActions, PlanningTreeRowColumnProvider rowColumnProviderToUse) throws Exception
	{
		super(mainWindowToUse, treeToUse, modelToUse, buttonActions, rowColumnProviderToUse);
	}
	
	public static PlanningTreeTablePanel createTreeTablePanel(MainWindow mainWindowToUse, TreeTableNode rootNode, PlanningTreeRowColumnProvider rowColumnProvider) throws Exception
	{
		TreeTableModelWithRebuilder model = createViabilityTreeTableModel(mainWindowToUse.getProject(), rootNode, rowColumnProvider);
		ViabilityTreeTable treeTable = new ViabilityTreeTable(mainWindowToUse, model);

		return new TargetViabilityTreeTablePanel(mainWindowToUse, treeTable, model, getButtonActions(), rowColumnProvider);
	}
	
	public static PlanningTreeTablePanel createTreeTableWithVisibleRootPanel(MainWindow mainWindowToUse) throws Exception
	{
		TreeTableNode rootNode = new PlanningTreeRootNodeAlwaysExpanded(mainWindowToUse.getProject());
		PlanningTreeRowColumnProvider rowColumnProvider = new TargetViabilityRowColumnProvider(mainWindowToUse.getProject());
		
		TreeTableModelWithRebuilder model = createViabilityTreeTableModel(mainWindowToUse.getProject(), rootNode, rowColumnProvider);
		ViabilityTreeTable treeTable = new ViabilityTreeTableWithVisibleRootNode(mainWindowToUse, model);

		return new TargetViabilityTreeTablePanel(mainWindowToUse, treeTable, model, getButtonActions(), rowColumnProvider);
	}

	private static ViabilityTreeTableModel createViabilityTreeTableModel(final Project project, TreeTableNode rootNode, PlanningTreeRowColumnProvider rowColumnProvider) throws Exception
	{
		return new ViabilityTreeTableModel(project, rootNode, rowColumnProvider);
	}
	
	@Override
	protected PlanningViewAbstractTreeTableSyncedTableModel createMainTableModel(final PlanningTreeRowColumnProvider rowColumnProviderToUse) throws Exception
	{
		return new ViabilityViewMainTableModel(getProject(), getTree(), rowColumnProviderToUse);
	}

	@Override
	protected PlanningUpperMultiTable createUpperMultiTable(PlanningTreeTable treeToUse, PlanningTreeMultiTableModel multiModelToUse)
	{
		return new ViabilityUpperMultiTable(getMainWindow(), treeToUse, multiModelToUse);
	}
	
	@Override
	protected boolean shouldSetFocusOnFirstField()
	{
		return false;
	}

	private static Class[] getButtonActions()
	{
		return new Class[] {
				ActionCreateKeyEcologicalAttribute.class, 
				ActionCreateKeyEcologicalAttributeIndicator.class,
				ActionCreateKeyEcologicalAttributeMeasurement.class,
				ActionExpandToMenu.class,
				
				ActionDeleteKeyEcologicalAttribute.class,
				ActionDeletePlanningViewTreeNode.class,
		};
	}
}
