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

import java.awt.Dimension;

import org.miradi.actions.ActionCreateKeyEcologicalAttribute;
import org.miradi.actions.ActionCreateKeyEcologicalAttributeIndicator;
import org.miradi.actions.ActionCreateKeyEcologicalAttributeMeasurement;
import org.miradi.actions.ActionDeleteKeyEcologicalAttribute;
import org.miradi.actions.ActionDeleteKeyEcologicalAttributeIndicator;
import org.miradi.actions.ActionDeleteKeyEcologicalAttributeMeasurement;
import org.miradi.actions.ActionExpandToMenu;
import org.miradi.dialogs.treetables.GenericTreeTableModel;
import org.miradi.dialogs.treetables.TreeTablePanelWithFourButtonColumns;
import org.miradi.dialogs.treetables.TreeTableWithStateSaving;
import org.miradi.main.AppPreferences;
import org.miradi.main.CommandExecutedEvent;
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.AbstractTarget;
import org.miradi.objects.BaseObject;
import org.miradi.objects.Factor;
import org.miradi.objects.HumanWelfareTarget;
import org.miradi.objects.Indicator;
import org.miradi.objects.KeyEcologicalAttribute;
import org.miradi.objects.Measurement;
import org.miradi.objects.Target;
import org.miradi.project.Project;

public class TargetViabililtyTreeTablePanel extends TreeTablePanelWithFourButtonColumns
{
	public TargetViabililtyTreeTablePanel(MainWindow mainWindowToUse, Project projectToUse, TreeTableWithStateSaving treeToUse) throws Exception
	{
		this( mainWindowToUse, treeToUse, buttonActions);
	}

	public TargetViabililtyTreeTablePanel(MainWindow mainWindowToUse, TreeTableWithStateSaving treeToUse, Class[] buttonActionToUse) throws Exception
	{
		super(mainWindowToUse, treeToUse, buttonActionToUse);
		treeTableScrollPane.getViewport().setBackground(AppPreferences.getDataPanelBackgroundColor());
	}
	
	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(super.getPreferredSize().width, 100);
	}

	//FIXME low: This code needs to be analyzed
	// to see if it really needs to rebuld its tree under all these conditions
	// Also, taskTreeTablePanel and TargetViabilityTreeTablePanel should have very similar 
	// CommandExecuted methods with very similar structures
	@Override
	public void commandExecuted(CommandExecutedEvent event)
	{
		GenericTreeTableModel treeTableModel = getTreeTableModel();
		final boolean wereTargetKEANodesAddedOrRemoved = event.isSetDataCommandWithThisTypeAndTag(Target.getObjectType(), AbstractTarget.TAG_KEY_ECOLOGICAL_ATTRIBUTE_IDS);
		final boolean wereHumanWelfareTargetKEANodesAddedOrRemoved = event.isSetDataCommandWithThisTypeAndTag(HumanWelfareTarget.getObjectType(), AbstractTarget.TAG_KEY_ECOLOGICAL_ATTRIBUTE_IDS);
		
		final boolean wereIndicatorNodesAddedOrRemoved = event.isSetDataCommandWithThisTypeAndTag(ObjectType.KEY_ECOLOGICAL_ATTRIBUTE, KeyEcologicalAttribute.TAG_INDICATOR_IDS);

		final boolean wereMeasuremetNodesAddedOrRemoved = event.isSetDataCommandWithThisTypeAndTag(Indicator.getObjectType(), Indicator.TAG_MEASUREMENT_REFS);
		
		final boolean wereFactorIndicatorNodesAddedOrRemoved = event.isFactorSetDataCommandWithThisTypeAndTag(Factor.TAG_INDICATOR_IDS);
		
		final boolean wereNodesAddedOrRemoved = wereTargetKEANodesAddedOrRemoved ||
												wereHumanWelfareTargetKEANodesAddedOrRemoved ||
												wereIndicatorNodesAddedOrRemoved ||
												wereMeasuremetNodesAddedOrRemoved ||
												wereFactorIndicatorNodesAddedOrRemoved;
		
		final boolean wasTargetModeChanged = isTargetModeChange(event);
		
		if(wereNodesAddedOrRemoved || wasTargetModeChanged)
		{
			ORef selectedRef = ORef.INVALID;
			BaseObject selectedObject = getSelectedObject();
			if(selectedObject != null)
				selectedRef = selectedObject.getRef();
				
			treeTableModel.rebuildEntireTree();
			restoreTreeExpansionState();
			
			tree.selectObjectAfterSwingClearsItDueToTreeStructureChange(new ORefList(selectedRef), 0);
		} 
		else if(isTreeExpansionCommand(event))
		{
			restoreTreeExpansionState();	
		}
		
		validateModifiedObject(event, KeyEcologicalAttribute.getObjectType());
		validateModifiedObject(event, Target.getObjectType());
		validateModifiedObject(event, HumanWelfareTarget.getObjectType());
		validateModifiedObject(event, Indicator.getObjectType());
		validateModifiedObject(event, Measurement.getObjectType());
		
		repaintToGrowIfTreeIsTaller();	
	}

	private boolean isTargetModeChange(CommandExecutedEvent event)
	{
		if (event.isSetDataCommandWithThisTypeAndTag(Target.getObjectType(), Target.TAG_VIABILITY_MODE))
			return true;
		
		return event.isSetDataCommandWithThisTypeAndTag(HumanWelfareTarget.getObjectType(), Target.TAG_VIABILITY_MODE);
	}
	
	private void validateModifiedObject(CommandExecutedEvent event, int type)
	{
		if(isSelectedObjectModification(event, type))
		{
			validate();
		}
	}

	static final Class[] buttonActions = new Class[] {
			ActionCreateKeyEcologicalAttribute.class, 
			ActionCreateKeyEcologicalAttributeIndicator.class,
			ActionCreateKeyEcologicalAttributeMeasurement.class,
			
			ActionDeleteKeyEcologicalAttribute.class,
			ActionDeleteKeyEcologicalAttributeIndicator.class,
			ActionDeleteKeyEcologicalAttributeMeasurement.class,
			ActionExpandToMenu.class,
			};
}
