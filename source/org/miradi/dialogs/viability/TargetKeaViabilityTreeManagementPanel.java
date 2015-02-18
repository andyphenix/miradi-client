/* 
Copyright 2005-2015, Foundations of Success, Bethesda, Maryland
on behalf of the Conservation Measures Partnership ("CMP").
Material developed between 2005-2013 is jointly copyright by Beneficent Technology, Inc. ("The Benetech Initiative"), Palo Alto, California.

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

import javax.swing.Icon;

import org.miradi.actions.jump.ActionJumpTargetViability3Step;
import org.miradi.dialogs.planning.treenodes.PlanningTreeAlwaysExpandedBaseObjectNode;
import org.miradi.dialogs.planning.upperPanel.PlanningTreeTablePanel;
import org.miradi.icons.IconManager;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ORef;
import org.miradi.objects.BaseObject;

public class TargetKeaViabilityTreeManagementPanel extends AbstractViabilityManagementPanel
{
	private TargetKeaViabilityTreeManagementPanel(MainWindow mainWindowToUse, PlanningTreeTablePanel tablePanelToUse, TargetViabilityMultiPropertiesPanel propertiesPanel) throws Exception
	{
		super(mainWindowToUse, tablePanelToUse, propertiesPanel);
	}
	
	public static AbstractViabilityManagementPanel createManagementPanel(MainWindow mainWindowToUse, ORef parentRefToUse) throws Exception
	{
		BaseObject parent = BaseObject.find(mainWindowToUse.getProject(), parentRefToUse);
		PlanningTreeAlwaysExpandedBaseObjectNode rootNode = new PlanningTreeAlwaysExpandedBaseObjectNode(mainWindowToUse.getProject(), null, parent);
		KeaViabilityRowColumnProvider rowColumnProvider = new KeaViabilityRowColumnProvider(mainWindowToUse.getProject());
		
		PlanningTreeTablePanel treeTablePanel = TargetViabilityTreeTablePanel.createTreeTablePanel(mainWindowToUse, rootNode, rowColumnProvider);
		TargetViabilityMultiPropertiesPanel propertiesPanel = new TargetViabilityMultiPropertiesPanel(mainWindowToUse);
		
		return new TargetKeaViabilityTreeManagementPanel(mainWindowToUse, treeTablePanel, propertiesPanel);
	}
	
	@Override
	public Icon getIcon()
	{
		return IconManager.getKeyEcologicalAttributeIcon();
	}
	
	@Override
	public Class getJumpActionClass()
	{
		return ActionJumpTargetViability3Step.class;
	}
	
	@Override
	public String getSplitterDescription()
	{
		return PANEL_DESCRIPTION + VIABILITY_SPLITTER_TAG + SPLITTER_TAG;
	}
	
	@Override
	public String getPanelDescription()
	{
		return PANEL_DESCRIPTION;
	} 

	private static String VIABILITY_SPLITTER_TAG = "KEA";
	private static String PANEL_DESCRIPTION = EAM.text("Tab|Viability");
}
