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
package org.miradi.dialogs.viability;

import org.miradi.actions.jump.ActionJumpTargetViability3Step;
import org.miradi.dialogs.planning.treenodes.PlanningTreeBaseObjectNode;
import org.miradi.dialogs.planning.upperPanel.PlanningTreeTablePanel;
import org.miradi.dialogs.planning.upperPanel.TargetViabilityTreeTableModel;
import org.miradi.dialogs.planning.upperPanel.TreeTableModelWithRebuilder;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ORef;

public class TargetPropertiesKeaViabilityTreeManagementPanel extends TargetViabilityManagementPanelNew
{
	private TargetPropertiesKeaViabilityTreeManagementPanel(MainWindow mainWindowToUse, PlanningTreeTablePanel tablePanelToUse, TargetViabilityMultiPropertiesPanel propertiesPanel) throws Exception
	{
		super(mainWindowToUse, tablePanelToUse, propertiesPanel);
	}
	
	public static TargetViabilityManagementPanelNew createKeaViabilityManagementPanel(MainWindow mainWindowToUse, ORef parentRefToUse) throws Exception
	{
		PlanningTreeBaseObjectNode rootNode = new PlanningTreeBaseObjectNode(mainWindowToUse.getProject(), null, parentRefToUse);
		KeaViabilityRowColumnProvider rowColumnProvider = new KeaViabilityRowColumnProvider(mainWindowToUse.getProject());
		
		TreeTableModelWithRebuilder model = new TargetViabilityTreeTableModel(mainWindowToUse.getProject(), rootNode, rowColumnProvider);
		PlanningTreeTablePanel treeTablePanel = TargetViabilityTreeTablePanel.createTreeTablePanel(mainWindowToUse, model, rowColumnProvider);
		TargetViabilityMultiPropertiesPanel propertiesPanel = new TargetViabilityMultiPropertiesPanel(mainWindowToUse);
		
		return new TargetPropertiesKeaViabilityTreeManagementPanel(mainWindowToUse, treeTablePanel, propertiesPanel);
	}
	
	@Override
	public String getSplitterDescription()
	{
		return PANEL_DESCRIPTION_VIABILITY + SPLITTER_TAG;
	}

	@Override
	public Class getJumpActionClass()
	{
		return ActionJumpTargetViability3Step.class;
	}
	
	private static String PANEL_DESCRIPTION_VIABILITY = EAM.text("Tab|Viability"); 
}
