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

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;

import org.miradi.commands.CommandSetObjectData;
import org.miradi.dialogs.base.MiradiPanel;
import org.miradi.dialogs.planning.AccountingCodeBudgetDetailsTableModel;
import org.miradi.dialogs.planning.FundingSourceBudgetDetailsTableModel;
import org.miradi.dialogs.planning.RowColumnProvider;
import org.miradi.dialogs.planning.propertiesPanel.AboveBudgetColumnsBar;
import org.miradi.dialogs.planning.propertiesPanel.AbstractWorkUnitsTableModel;
import org.miradi.dialogs.planning.propertiesPanel.AccountingCodeExpenseTableModel;
import org.miradi.dialogs.planning.propertiesPanel.BudgetDetailsTableModel;
import org.miradi.dialogs.planning.propertiesPanel.ExpenseAmountsTableModel;
import org.miradi.dialogs.planning.propertiesPanel.FundingSourceExpenseTableModel;
import org.miradi.dialogs.planning.propertiesPanel.PlanningViewMainModelExporter;
import org.miradi.dialogs.planning.propertiesPanel.PlanningWorkUnitsTableModel;
import org.miradi.dialogs.planning.propertiesPanel.ProjectResourceFilterStatusPanel;
import org.miradi.dialogs.planning.propertiesPanel.ProjectResourceWorkUnitsTableModel;
import org.miradi.dialogs.tablerenderers.FontForObjectTypeProvider;
import org.miradi.dialogs.tablerenderers.PlanningViewFontProvider;
import org.miradi.dialogs.treetables.TreeTablePanelWithSixButtonColumns;
import org.miradi.dialogs.treetables.TreeTableWithStateSaving;
import org.miradi.main.CommandExecutedEvent;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.BaseObject;
import org.miradi.objects.Factor;
import org.miradi.objects.Indicator;
import org.miradi.objects.KeyEcologicalAttribute;
import org.miradi.objects.Measurement;
import org.miradi.objects.Objective;
import org.miradi.objects.ResourceAssignment;
import org.miradi.objects.Strategy;
import org.miradi.objects.TableSettings;
import org.miradi.objects.Target;
import org.miradi.objects.Task;
import org.miradi.questions.CustomPlanningColumnsQuestion;
import org.miradi.utils.CodeList;
import org.miradi.utils.MultiTableCombinedAsOneExporter;
import org.miradi.utils.MultiTableRowHeightController;
import org.miradi.utils.MultiTableVerticalScrollController;
import org.miradi.utils.MultiTableSelectionController;
import org.miradi.utils.TableExporter;
import org.miradi.utils.TreeTableExporter;

abstract public class PlanningTreeTablePanel extends TreeTablePanelWithSixButtonColumns
{
	protected PlanningTreeTablePanel(MainWindow mainWindowToUse, PlanningTreeTable treeToUse, PlanningTreeTableModel modelToUse, Class[] buttonActions, RowColumnProvider rowColumnProviderToUse) throws Exception
	{
		super(mainWindowToUse, treeToUse, buttonActions);
		
		rowColumnProvider = rowColumnProviderToUse;
		model = modelToUse;
		
		treeTableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		MultiTableRowHeightController rowHeightController = new MultiTableRowHeightController(getMainWindow());
		rowHeightController.addTable(treeToUse);
		
		MultiTableSelectionController selectionController = new MultiTableSelectionController(this);
		selectionController.addTable(treeToUse);
		
		MultiTableVerticalScrollController scrollController = new MultiTableVerticalScrollController();
		scrollController.addScrollPane(treeTableScrollPane);
		
		listenForColumnWidthChanges(getTree());
		
		mainModel = new PlanningViewMainTableModel(getProject(), treeToUse, rowColumnProvider);
		multiModel = new PlanningTreeMultiTableModel(treeToUse.getUniqueTableIdentifier());
		measurementModel = new PlanningViewMeasurementTableModel(getProject(), treeToUse);
		futureStatusModel = new PlanningViewFutureStatusTableModel(getProject(), treeToUse);
		workUnitsTableModel = new PlanningWorkUnitsTableModel(getProject(), treeToUse, modelToUse.getUniqueTreeTableModelIdentifier());
		resourceWorkUnitsTableModel = new ProjectResourceWorkUnitsTableModel(getProject(), treeToUse, modelToUse.getUniqueTreeTableModelIdentifier());
		expenseAmountsTableModel = new ExpenseAmountsTableModel(getProject(), treeToUse, modelToUse.getUniqueTreeTableModelIdentifier());
		budgetDetailsTableModel = new BudgetDetailsTableModel(getProject(), treeToUse, modelToUse.getUniqueTreeTableModelIdentifier());
		fundingSourceBudgetDetailsTableModel = new FundingSourceBudgetDetailsTableModel(getProject(), treeToUse, modelToUse.getUniqueTreeTableModelIdentifier());
		accoundingCodeBudgetDetailsTableModel = new AccountingCodeBudgetDetailsTableModel(getProject(), treeToUse, modelToUse.getUniqueTreeTableModelIdentifier());
		fundingSourceExpenseTableModel = new FundingSourceExpenseTableModel(getProject(), treeToUse, modelToUse.getUniqueTreeTableModelIdentifier());
		accountingCodeExpenseTableModel = new AccountingCodeExpenseTableModel(getProject(), treeToUse, modelToUse.getUniqueTreeTableModelIdentifier());
		
		FontForObjectTypeProvider fontProvider = new PlanningViewFontProvider(getMainWindow());
		mainTable = new PlanningUpperMultiTable(treeToUse, multiModel, fontProvider);
		
		mainTableScrollPane = integrateTable(treeTableScrollPane.getVerticalScrollBar(), scrollController, rowHeightController, selectionController, treeToUse, mainTable);
		mainTableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		// NOTE: Replace treeScrollPane that super constructor added
		removeAll();
		add(buttonBox, BorderLayout.BEFORE_FIRST_LINE);
		
		JPanel leftPanel = new MiradiPanel(new BorderLayout());
		filterStatusPanel = new ProjectResourceFilterStatusPanel(getProject());
		leftPanel.add(filterStatusPanel, BorderLayout.BEFORE_FIRST_LINE);
		leftPanel.add(treeTableScrollPane, BorderLayout.CENTER);
		
		JPanel rightPanel = new MiradiPanel(new BorderLayout());
		AboveBudgetColumnsBar aboveMainTableBar = new AboveBudgetColumnsBar(mainTable);
		aboveMainTableBar.setTableScrollPane(mainTableScrollPane);
		rightPanel.add(aboveMainTableBar, BorderLayout.BEFORE_FIRST_LINE);
		rightPanel.add(mainTableScrollPane, BorderLayout.CENTER);
		
		add(leftPanel, BorderLayout.BEFORE_LINE_BEGINS);
		add(rightPanel, BorderLayout.CENTER);
		
		rebuildEntireTreeAndTable();
	
		mainTableColumnSelectionListener = new MainTableSelectionHandler();
		treeTableRowSelectionListener = new TreeTableRowSelectionHandler();
		
		enableSelectionListeners();
		enableSectionSwitch();
	}

	@Override
	public void dispose()
	{
		super.dispose();
		
		mainTable.dispose();
	}
	
	public void commandExecuted(CommandExecutedEvent event)
	{
		try
		{		
			if (doesCommandForceRebuild(event))
				rebuildEntireTreeAndTable();
			
			if(doesAffectTableRowHeight(event))
				mainTable.updateAutomaticRowHeights();
			
			if(isTreeExpansionCommand(event))
				restoreTreeExpansionState();
			
			if(isSelectedObjectModification(event, ResourceAssignment.getObjectType()))
				validate();
		
			repaintToGrowIfTreeIsTaller();
		}
		catch(Exception e)
		{
			EAM.logException(e);
			EAM.errorDialog("Error occurred: " + e.getMessage());
		}
		
	}

	private boolean doesAffectTableRowHeight(CommandExecutedEvent event)
	{
		if (!event.isSetDataCommand())
			return false;
		
		CommandSetObjectData setCommand = (CommandSetObjectData) event.getCommand();
		ORef affectedObjectRef = setCommand.getObjectORef();
		
		return isAffectedRefFoundInMainTableModel(affectedObjectRef);
	}

	private boolean isAffectedRefFoundInMainTableModel(ORef affectedObjectRef)
	{
		for (int row = 0; row < mainModel.getRowCount(); ++row)
		{
			BaseObject baseObjectForRow = mainModel.getBaseObjectForRow(row);
			if (baseObjectForRow != null && baseObjectForRow.getRef().equals(affectedObjectRef))
				return true;
		}
		
		return false;
	}

	protected boolean doesCommandForceRebuild(CommandExecutedEvent event)
	{
		if (isColumnExpandCollapseCommand(event))
			return true;
		
		if(didAffectResourceAssignmentsAndExpenseAssignments(event))
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
		
		return false;
	}
	
	private  boolean isColumnExpandCollapseCommand(CommandExecutedEvent event)
	{
		return event.isSetDataCommandWithThisTypeAndTag(TableSettings.getObjectType(), TableSettings.TAG_DATE_UNIT_LIST_DATA);
	}
	
	protected boolean wasTypeCreatedOrDeleted(CommandExecutedEvent event, int objectType)
	{
		if (event.isCreateCommandForThisType(objectType))
			return true;
		
		return event.isDeleteCommandForThisType(objectType);
	}

	private boolean didAffectResourceAssignmentsAndExpenseAssignments(CommandExecutedEvent event)
	{
		if (event.isSetDataCommandWithThisTag(BaseObject.TAG_RESOURCE_ASSIGNMENT_IDS))
			return true;
		
		if (event.isSetDataCommandWithThisTag(BaseObject.TAG_EXPENSE_ASSIGNMENT_REFS))
			return true;
		
		if (event.isSetDataCommandWithThisTypeAndTag(ResourceAssignment.getObjectType(), ResourceAssignment.TAG_RESOURCE_ID))
			return true;
			
		return false;
	}

	private boolean didAffectMeasurementInTree(CommandExecutedEvent event)
	{
		return event.isSetDataCommandWithThisTypeAndTag(Indicator.getObjectType(), Indicator.TAG_MEASUREMENT_REFS);
	}
	
	private boolean didAffectTableSettingsMapForBudgetColumns(CommandExecutedEvent event)
	{
		return event.isSetDataCommandWithThisTypeAndTag(TableSettings.getObjectType(), TableSettings.TAG_TABLE_SETTINGS_MAP);
	}

	private boolean isTargetModeChange (CommandExecutedEvent event)
	{
		return event.isSetDataCommandWithThisTypeAndTag(Target.getObjectType(), Target.TAG_VIABILITY_MODE);
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

	private boolean didAffectIndicatorInTree(CommandExecutedEvent event)
	{
		if (! event.isSetDataCommand())
			return false;
		
		CommandSetObjectData setCommand = (CommandSetObjectData) event.getCommand();
		int type = setCommand.getObjectType();
		String tag = setCommand.getFieldTag();
		if(Factor.isFactor(type))
			return isValidFactorTag(tag);
		
		if(type == KeyEcologicalAttribute.getObjectType() && tag.equals(KeyEcologicalAttribute.TAG_INDICATOR_IDS))
			return true;
				
		return false;
	}
	
	private boolean isValidFactorTag(String relevancyTag)
	{
		if (relevancyTag.equals(Factor.TAG_INDICATOR_IDS))
				return true;
		
		if (relevancyTag.equals(Factor.TAG_OBJECTIVE_IDS))
			return true;
		
		return false;
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
		if(type == Task.getObjectType() && tag.equals(Task.TAG_SUBTASK_IDS))
			return true;
		if(type == Strategy.getObjectType() && tag.equals(Strategy.TAG_ACTIVITY_IDS))
			return true;
		if(type == Indicator.getObjectType() && tag.equals(Indicator.TAG_METHOD_IDS))
			return true;
		
		return false;
	}
	
	private void rebuildEntireTreeAndTable() throws Exception
	{
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

	private void rebuildEntireTreeTable() throws Exception
	{
		int selectedRow = tree.getSelectionModel().getMinSelectionIndex();
		int selectedColumn = mainTable.getColumnModel().getSelectionModel().getMinSelectionIndex();

		// TODO: Perhaps possibly detect exactly what changed and 
		// only rebuild the columns or the rows rather than always doing both
	
		// NOTE: The following rebuild the columns but don't touch the tree
		getPlanningModel().updateColumnsToShow();
		tree.rebuildTableCompletely();

		updateResourceFilter();
		multiModel.updateColumnsToShow();
		
		// NOTE: The following rebuild the tree but don't touch the columns
		getPlanningModel().rebuildEntireTree();
		measurementModel.fireTableDataChanged();
		futureStatusModel.fireTableDataChanged();
		workUnitsTableModel.fireTableDataChanged();
		resourceWorkUnitsTableModel.fireTableDataChanged();
		fundingSourceExpenseTableModel.fireTableDataChanged();
		accountingCodeExpenseTableModel.fireTableDataChanged();
		expenseAmountsTableModel.fireTableDataChanged();
		budgetDetailsTableModel.fireTableDataChanged();
		fundingSourceBudgetDetailsTableModel.fireTableDataChanged();
		accoundingCodeBudgetDetailsTableModel.fireTableDataChanged();
		restoreTreeExpansionState();
		updateRightSideTablePanels();

		filterStatusPanel.updateStatusLabel();
	
		selectObjectAfterSwingClearsItDueToTreeStructureChange(getMainTable(), selectedRow, selectedColumn);
	}
	
	protected void updateResourceFilter() throws Exception
	{
	}

	private void updateRightSideTablePanels() throws Exception
	{
		multiModel.removeAllModels();
		multiModel.addModel(mainModel);

		mainTableScrollPane.showVerticalScrollBar();

		CodeList columnsToShow = getColumnsToShow();
		if (columnsToShow.contains(Measurement.META_COLUMN_TAG))
			multiModel.addModel(measurementModel);

		if (columnsToShow.contains(Indicator.META_COLUMN_TAG))
			multiModel.addModel(futureStatusModel);
		
		if (shouldShow(CustomPlanningColumnsQuestion.META_RESOURCE_ASSIGNMENT_COLUMN_CODE))
			multiModel.addModel(workUnitsTableModel);
		
		if (shouldShow(CustomPlanningColumnsQuestion.META_PROJECT_RESOURCE_WORK_UNITS_COLUMN_CODE))
			multiModel.addModel(resourceWorkUnitsTableModel);
		
		if (shouldShow(CustomPlanningColumnsQuestion.META_EXPENSE_ASSIGNMENT_COLUMN_CODE))
			multiModel.addModel(expenseAmountsTableModel);
		
		if (shouldShow(CustomPlanningColumnsQuestion.META_FUNDING_SOURCE_EXPENSE_COLUMN_CODE))
			multiModel.addModel(fundingSourceExpenseTableModel);
		
		if (shouldShow(CustomPlanningColumnsQuestion.META_ACCOUNTING_CODE_EXPENSE_COLUMN_CODE))
			multiModel.addModel(accountingCodeExpenseTableModel);
		
		if (shouldShow(CustomPlanningColumnsQuestion.META_FUNDING_SOURCE_BUDGET_DETAILS_COLUMN_CODE))
			multiModel.addModel(fundingSourceBudgetDetailsTableModel);
		
		if (shouldShow(CustomPlanningColumnsQuestion.META_ACCOUNTING_CODE_BUDGET_DETAILS_COLUMN_CODE))
			multiModel.addModel(accoundingCodeBudgetDetailsTableModel);
		
		if (shouldShow(CustomPlanningColumnsQuestion.META_BUDGET_DETAIL_COLUMN_CODE))
			multiModel.addModel(budgetDetailsTableModel);
		
		mainTable.updateToReflectNewColumns();
		validate();
		repaint();
	}

	private boolean shouldShow(String metaColumnCode) throws Exception
	{
		return getColumnsToShow().contains(metaColumnCode);
	}

	private CodeList getColumnsToShow() throws Exception
	{
		return getRowColumnProvider().getColumnListToShow();
	}

	private PlanningTreeTableModel getPlanningModel()
	{
		return (PlanningTreeTableModel)getModel();
	}
	
	public TableExporter getTableForExporting() throws Exception
	{
		MultiTableCombinedAsOneExporter multiTableExporter = new MultiTableCombinedAsOneExporter(getProject());
		multiTableExporter.addAsMasterTable(new TreeTableExporter(getTree()));
		multiTableExporter.addExportable(new PlanningViewMainModelExporter(getProject(), multiModel, getTree(), multiModel.getUniqueTableModelIdentifier()));
		
		return multiTableExporter;
	}
	
	protected PlanningUpperMultiTable getMainTable()
	{
		return mainTable;
	}
	
	public RowColumnProvider getRowColumnProvider()
	{
		return rowColumnProvider;
	}
	
	protected AbstractWorkUnitsTableModel getWorkUnitsTableModel()
	{
		return workUnitsTableModel;
	}
	
	protected BudgetDetailsTableModel getBudgetDetailsTableModel()
	{
		return budgetDetailsTableModel;
	}
	
	protected PlanningViewMainTableModel getMainModel()
	{
		return mainModel;
	}
	
	public void beginSelectionChangingProcess()
	{
		disableSectionSwitchDuringFullRebuild();
	}
	
	public void endSelectionChangingProcess()
	{
		enableSectionSwitch();
	}
	
	private boolean isSideTabSwitchingDisabled()
	{
		return !disableSideTabSwitching;
	}
	
	private void disableSectionSwitchDuringFullRebuild()
	{
		disableSideTabSwitching = false;
	}
	
	private void enableSectionSwitch()
	{
		disableSideTabSwitching = true;
	}
	
	private void enableSelectionListeners()
	{
		listenForColumnSelectionChanges(getMainTable());
		listenForTreeTableRowSelectionChanges(getTree());
	}
	
	private void listenForColumnSelectionChanges(JTable table)
	{
		table.getColumnModel().addColumnModelListener(mainTableColumnSelectionListener);
		table.getSelectionModel().addListSelectionListener(mainTableColumnSelectionListener);
	}

	private void listenForTreeTableRowSelectionChanges(TreeTableWithStateSaving treeToUse)
	{
		treeToUse.addSelectionChangeListener(treeTableRowSelectionListener);
	}
	
	private void selectSectionForTag(String selectedColumnTag)
	{
		if (isSideTabSwitchingDisabled())
			return;
		
		if (getPropertiesPanel() == null)
			return;
		
		getPropertiesPanel().selectSectionForTag(selectedColumnTag);
	}
	
	class TreeTableRowSelectionHandler implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent e)
		{
			selectSectionForTag(BaseObject.TAG_LABEL);
		}
	}
	
	class MainTableSelectionHandler  implements TableColumnModelListener, ListSelectionListener
	{
		public void columnAdded(TableColumnModelEvent e)
		{
		}

		public void columnMarginChanged(ChangeEvent e)
		{
		}

		public void columnMoved(TableColumnModelEvent e)
		{
		}

		public void columnRemoved(TableColumnModelEvent e)
		{
		}

		public void columnSelectionChanged(ListSelectionEvent e)
		{
			selectSectionForColumn();
		}

		public void valueChanged(ListSelectionEvent e)
		{
			selectSectionForColumn();
		}
		
		private void selectSectionForColumn()
		{
			String selectedColumnTag = getSelectedColumnTag();
			selectSectionForTag(selectedColumnTag);
		}

		private String getSelectedColumnTag()
		{
			int selectedColumn = mainTable.getSelectedColumn();
			if (selectedColumn < 0)
				return "";
			
			int modelColumn = mainTable.convertColumnIndexToModel(selectedColumn);
			return multiModel.getTagForCell(getSafeTypeForSelectedRow(), modelColumn);
		}		
	}
	
	private int getSafeTypeForSelectedRow()
	{
		if (getTree().getSelectedObjects().length > 0)
		{
			BaseObject selectedObject = getTree().getSelectedObjects()[0];
			return selectedObject.getType();
		}
		
		return ObjectType.FAKE;
	
	}
	
	public void selectObjectAfterSwingClearsItDueToTreeStructureChange(JTable table, int fallbackRow, int fallbackColumn)
	{
		SwingUtilities.invokeLater(new Reselecter(table, fallbackRow, fallbackColumn));
	}
	
	class Reselecter implements Runnable
	{
		public Reselecter(JTable tableToUse, int rowToSelect, int columnToSelect)
		{
			table = tableToUse;
			row = rowToSelect;
			column = columnToSelect;
		}
		
		public void run()
		{
			disableSectionSwitchDuringFullRebuild();
			try
			{
				table.getSelectionModel().setSelectionInterval(row, row);
				table.getColumnModel().getSelectionModel().setSelectionInterval(column, column);
			}
			finally 
			{
				enableSectionSwitch();
			}
		}
		
		private JTable table;
		private int row;
		private int column;
	}
	
	private RowColumnProvider rowColumnProvider;
	private PlanningViewMainTableModel mainModel;
	private PlanningTreeMultiTableModel multiModel;
	private PlanningUpperMultiTable mainTable;

	private PlanningViewMeasurementTableModel measurementModel;
	private PlanningViewFutureStatusTableModel futureStatusModel;
	protected AbstractWorkUnitsTableModel workUnitsTableModel;
	private ExpenseAmountsTableModel expenseAmountsTableModel;
	protected BudgetDetailsTableModel budgetDetailsTableModel;
	private FundingSourceBudgetDetailsTableModel fundingSourceBudgetDetailsTableModel;
	private AccountingCodeBudgetDetailsTableModel accoundingCodeBudgetDetailsTableModel;
	private ProjectResourceWorkUnitsTableModel resourceWorkUnitsTableModel;
	private FundingSourceExpenseTableModel fundingSourceExpenseTableModel;
	private AccountingCodeExpenseTableModel accountingCodeExpenseTableModel;

	private ScrollPaneWithHideableScrollBar mainTableScrollPane;
	private ProjectResourceFilterStatusPanel filterStatusPanel;
	
	private MainTableSelectionHandler mainTableColumnSelectionListener;
	private TreeTableRowSelectionHandler treeTableRowSelectionListener;
	private boolean disableSideTabSwitching;
}
