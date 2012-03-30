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
package org.miradi.dialogs.treetables;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.miradi.commands.CommandSetObjectData;
import org.miradi.dialogs.base.EditableObjectTableModel;
import org.miradi.dialogs.base.MiradiPanel;
import org.miradi.main.CommandExecutedEvent;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ORef;
import org.miradi.objects.AbstractTarget;
import org.miradi.objects.BaseObject;
import org.miradi.objects.ExpenseAssignment;
import org.miradi.objects.Factor;
import org.miradi.objects.HumanWelfareTarget;
import org.miradi.objects.Indicator;
import org.miradi.objects.KeyEcologicalAttribute;
import org.miradi.objects.Measurement;
import org.miradi.objects.ObjectTreeTableConfiguration;
import org.miradi.objects.Objective;
import org.miradi.objects.ProjectMetadata;
import org.miradi.objects.ResourceAssignment;
import org.miradi.objects.Strategy;
import org.miradi.objects.TableSettings;
import org.miradi.objects.Target;
import org.miradi.objects.Task;
import org.miradi.objects.ViewData;
import org.miradi.schemas.HumanWelfareTargetSchema;
import org.miradi.schemas.IndicatorSchema;
import org.miradi.schemas.KeyEcologicalAttributeSchema;
import org.miradi.schemas.MeasurementSchema;
import org.miradi.schemas.ObjectTreeTableConfigurationSchema;
import org.miradi.schemas.StrategySchema;
import org.miradi.schemas.TargetSchema;
import org.miradi.schemas.TaskSchema;
import org.miradi.utils.TableWithColumnWidthAndSequenceSaver;

abstract public class AbstractTreeTablePanel extends MultiTreeTablePanel
{
	public AbstractTreeTablePanel(MainWindow mainWindowToUse, TreeTableWithStateSaving treeToUse, Class[] buttonActionClasses) throws Exception
	{
		super(mainWindowToUse, treeToUse, buttonActionClasses);
	}
	
	@Override
	public void dispose()
	{
		getMainTable().dispose();
		
		super.dispose();
	}
	
	protected void createTreeAndTablePanel() throws Exception
	{
		// NOTE: Replace treeScrollPane that super constructor added
		removeAll();
		add(getButtonBox(), BorderLayout.BEFORE_FIRST_LINE);
		
		JPanel leftPanel = new MiradiPanel(new BorderLayout());
		addAboveTreeStatusPanel(leftPanel);
		leftPanel.add(getTreeTableScrollPane(), BorderLayout.CENTER);
		
		JPanel rightPanel = new MiradiPanel(new BorderLayout());
		addAboveColumnBar(rightPanel);
		rightPanel.add(getMainTableScrollPane(), BorderLayout.CENTER);
		
		add(leftPanel, BorderLayout.BEFORE_LINE_BEGINS);
		add(rightPanel, BorderLayout.CENTER);
	}
	
	protected void addAboveTreeStatusPanel(JPanel leftPanel)
	{
	}

	protected void addAboveColumnBar(JPanel rightPanel)
	{
	}
	
	@Override
	protected void handleCommandEventWhileInactive(CommandExecutedEvent event)
	{
		try
		{
			if(doesCommandForceRebuild(event))
			{
				needsFullRebuild = true;
				EAM.logDebug("Queuing a rebuild for " + getClass().getSimpleName());
			}
		}
		catch(Exception e)
		{
			EAM.unexpectedErrorDialog(e);
		}
	}

	@Override
	public void becomeActive()
	{
		super.becomeActive();
		try
		{
			if(needsFullRebuild)
				rebuildEntireTreeAndTable();
		}
		catch(Exception e)
		{
			EAM.panic(e);
		}
	}

	@Override
	public void handleCommandEventImmediately(CommandExecutedEvent event)
	{
		try
		{		
			if (isColumnExpandCollapseCommand(event))
			{
				getMainTable().clearColumnSelection();
				rebuildEntireTreeAndTable();
			}
			
			
			if (doesCommandForceRebuild(event))
			{
				rebuildEntireTreeAndTable();
			}
			else if(doesAffectTableRowHeight(event))
			{
				getTree().updateAutomaticRowHeights();
				getMainTable().updateAutomaticRowHeights();
			}
			else if(event.isSetDataCommand())
			{
				validate();
			}
		
			
			if(isTreeExpansionCommand(event))
			{
				restoreTreeExpansionState();
			}
			
			repaintToGrowIfTreeIsTaller();
		}
		catch(Exception e)
		{
			EAM.logException(e);
			EAM.errorDialog("Error occurred: " + e.getMessage());
		}
	}
	
	protected void rebuildEntireTreeAndTable() throws Exception
	{
		if(!isActive())
		{
			needsFullRebuild = true;
			return;
		}
		
		needsFullRebuild = false;
		
		disableSectionSwitchDuringFullRebuild();
		try
		{
			rebuildEntireTreeTable();
		}
		finally
		{
			enableSectionSwitch();
		}
	}
	
	protected boolean isColumnExpandCollapseCommand(CommandExecutedEvent event)
	{
		return event.isSetDataCommandWithThisTypeAndTag(TableSettings.getObjectType(), TableSettings.TAG_DATE_UNIT_LIST_DATA);
	}
	
	protected boolean doesCommandForceRebuild(CommandExecutedEvent event) throws Exception
	{
		if(wereAssignmentNodesAddedOrRemoved(event))
			return true;
		
		if(wereProgressReportsAddedOrRemoved(event))
			return true;
		
		if(wereKeasAddedOrRemoved(event))
			return true;
		
		if(didAffectTaskInTree(event))
			return true;
		
		if(didAffectIndicatorInTree(event))
			return true;
		
		if(didAffectRelevancyInTree(event))
			return true;
		
		if(isTargetModeChange(event))
			return true;
		
		if(didAffectMeasurementInTree(event))
			return true;
		
		if(didAffectTableSettingsMapForBudgetColumns(event))
			return true;
		
		if(event.isSetDataCommandWithThisTypeAndTag(TableSettings.getObjectType(), TableSettings.TAG_WORK_PLAN_VISIBLE_NODES_CODE))
			return true;
		
		if (isCustomConfigurationCommand(event))
			return true;
		
		if (event.isSetDataCommandWithThisTypeAndTag(ObjectTreeTableConfigurationSchema.getObjectType(), ObjectTreeTableConfiguration.TAG_TARGET_NODE_POSITION))
			return true;
		
		if (event.isSetDataCommandWithThisTypeAndTag(ViewData.getObjectType(), ViewData.TAG_ACTION_TREE_CONFIGURATION_CHOICE))
			return true;
		
		if (event.isSetDataCommandWithThisTypeAndTag(ViewData.getObjectType(), ViewData.TAG_MONITORING_TREE_CONFIGURATION_CHOICE))
			return true;
		
		return false;
	}
	
	protected boolean wereAssignmentNodesAddedOrRemoved(CommandExecutedEvent event) throws Exception
	{
		return false;
	}
	
	private boolean wereProgressReportsAddedOrRemoved(CommandExecutedEvent event)
	{
		return event.isSetDataCommandWithThisTag(BaseObject.TAG_PROGRESS_REPORT_REFS);
	}
	
	private boolean wereKeasAddedOrRemoved(CommandExecutedEvent event)
	{
		if (event.isSetDataCommandWithThisTypeAndTag(HumanWelfareTargetSchema.getObjectType(), HumanWelfareTarget.TAG_KEY_ECOLOGICAL_ATTRIBUTE_IDS))
			return true;
		
		return event.isSetDataCommandWithThisTypeAndTag(TargetSchema.getObjectType(), Target.TAG_KEY_ECOLOGICAL_ATTRIBUTE_IDS);
	}
	
	//TODO this should use that getTasksTag (or something like that) method
	//from email :Please put a todo in isTaskMove that it should use that 
	//getTasksTag method (or whatever it's called) that I mentioned the 
	//other day. I know that one is my code not yours.
	private boolean didAffectTaskInTree(CommandExecutedEvent event)
	{
		if (! event.isSetDataCommand())
			return false;
		
		CommandSetObjectData setCommand = (CommandSetObjectData) event.getCommand();
		int type = setCommand.getObjectType();
		String tag = setCommand.getFieldTag();
		if(type == TaskSchema.getObjectType() && tag.equals(Task.TAG_SUBTASK_IDS))
			return true;
		if(type == StrategySchema.getObjectType() && tag.equals(Strategy.TAG_ACTIVITY_IDS))
			return true;
		if(type == IndicatorSchema.getObjectType() && tag.equals(Indicator.TAG_METHOD_IDS))
			return true;
		
		return false;
	}
	
	private boolean didAffectIndicatorInTree(CommandExecutedEvent event)
	{
		if (! event.isSetDataCommand())
			return false;
		
		CommandSetObjectData setCommand = (CommandSetObjectData) event.getCommand();
		int type = setCommand.getObjectType();
		String tag = setCommand.getFieldTag();
		if(Factor.isFactor(type))
			return isValidFactorTag(tag);
		
		if(type == KeyEcologicalAttributeSchema.getObjectType() && tag.equals(KeyEcologicalAttribute.TAG_INDICATOR_IDS))
			return true;
				
		return false;
	}
	
	private boolean isValidFactorTag(String relevancyTag)
	{
		if (relevancyTag.equals(Factor.TAG_INDICATOR_IDS))
				return true;
		
		if (relevancyTag.equals(Factor.TAG_OBJECTIVE_IDS))
			return true;
		
		if (relevancyTag.equals(AbstractTarget.TAG_GOAL_IDS))
			return true;
		
		return false;
	}
	
	private boolean didAffectRelevancyInTree(CommandExecutedEvent event)
	{
		if (! event.isSetDataCommand())
			return false;
		
		CommandSetObjectData setCommand = (CommandSetObjectData) event.getCommand();
		ORef ref = setCommand.getObjectORef();
		String tag = setCommand.getFieldTag();

		if(Objective.is(ref))
		{
			if(tag.equals(Objective.TAG_RELEVANT_STRATEGY_ACTIVITY_SET))
				return true;
			
			if(tag.equals(Objective.TAG_RELEVANT_INDICATOR_SET))
				return true;
		}

		return false;
	}
	
	private boolean isTargetModeChange (CommandExecutedEvent event)
	{
		return event.isSetDataCommandWithThisTypeAndTag(TargetSchema.getObjectType(), Target.TAG_VIABILITY_MODE);
	}
	
	private boolean didAffectMeasurementInTree(CommandExecutedEvent event)
	{
		if (event.isSetDataCommandWithThisTypeAndTag(IndicatorSchema.getObjectType(), Indicator.TAG_MEASUREMENT_REFS))
			return true;
		
		if (event.isSetDataCommandWithThisTypeAndTag(MeasurementSchema.getObjectType(), Measurement.TAG_DATE))
			return true;
		
		return false;
	}
	
	private boolean didAffectTableSettingsMapForBudgetColumns(CommandExecutedEvent event)
	{
		return event.isSetDataCommandWithThisTypeAndTag(TableSettings.getObjectType(), TableSettings.TAG_TABLE_SETTINGS_MAP);
	}
	
	private boolean isCustomConfigurationCommand(CommandExecutedEvent event)
	{
		if(event.isSetDataCommandWithThisTypeAndTag(ViewData.getObjectType(), ViewData.TAG_TREE_CONFIGURATION_REF))
			return true;
		
		if(event.isSetDataCommandWithThisTypeAndTag(ProjectMetadata.getObjectType(), ProjectMetadata.TAG_WORK_PLAN_DIAGRAM_DATA_INCLUSION))
			return true;
		
		if(event.isSetDataCommandWithThisTypeAndTag(ObjectTreeTableConfigurationSchema.getObjectType(), ObjectTreeTableConfiguration.TAG_COL_CONFIGURATION))
			return true;
		
		if(event.isSetDataCommandWithThisTypeAndTag(ObjectTreeTableConfigurationSchema.getObjectType(), ObjectTreeTableConfiguration.TAG_ROW_CONFIGURATION))
			return true;
		
		if(event.isSetDataCommandWithThisTypeAndTag(ObjectTreeTableConfigurationSchema.getObjectType(), ObjectTreeTableConfiguration.TAG_DIAGRAM_DATA_INCLUSION))
			return true;
		
		if(event.isSetDataCommandWithThisTypeAndTag(ObjectTreeTableConfigurationSchema.getObjectType(), ObjectTreeTableConfiguration.TAG_STRATEGY_OBJECTIVE_ORDER))
			return true;
				
		return false;
	}
	
	protected boolean doesAffectTableRowHeight(CommandExecutedEvent event)
	{
		if (!event.isSetDataCommand())
			return false;
		
		CommandSetObjectData setCommand = (CommandSetObjectData) event.getCommand();
		ORef affectedObjectRef = setCommand.getObjectORef();
		
		if(isAffectedRefFoundInMainTableModel(affectedObjectRef))
			return true;
		
		if(ResourceAssignment.is(affectedObjectRef))
			return true;
		
		if(ExpenseAssignment.is(affectedObjectRef))
			return true;
		
		return false;
	}

	private boolean isAffectedRefFoundInMainTableModel(ORef affectedObjectRef)
	{
		for (int row = 0; row < getMainModel().getRowCount(); ++row)
		{
			BaseObject baseObjectForRow = getMainModel().getBaseObjectForRow(row);
			if (baseObjectForRow != null && baseObjectForRow.getRef().equals(affectedObjectRef))
				return true;
		}
		
		return false;
	}
	
	protected boolean isSideTabSwitchingDisabled()
	{
		return (disableSideTabSwitchingCount > 0);
	}
	
	protected void disableSectionSwitchDuringFullRebuild()
	{
		++disableSideTabSwitchingCount;
	}
	
	protected void enableSectionSwitch()
	{
		if(disableSideTabSwitchingCount == 0)
		{
			EAM.logError("PlanningTreeTablePanel.enableSelectionSwitch called too many times");
			EAM.logStackTrace();
			return;
		}
		
		--disableSideTabSwitchingCount;
	}
	
	public ScrollPaneWithHideableScrollBar getMainTableScrollPane()
	{
		return mainTableScrollPane;
	}
	
	abstract protected EditableObjectTableModel getMainModel();

	abstract protected TableWithColumnWidthAndSequenceSaver getMainTable();
	
	abstract protected void rebuildEntireTreeTable() throws Exception;
	
	private int disableSideTabSwitchingCount;
	private boolean needsFullRebuild;
	protected ScrollPaneWithHideableScrollBar mainTableScrollPane;
}
