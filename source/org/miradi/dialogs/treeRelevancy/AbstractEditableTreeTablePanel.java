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
package org.miradi.dialogs.treeRelevancy;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

import org.miradi.dialogs.base.EditableObjectTable;
import org.miradi.dialogs.base.SingleBooleanColumnEditableModel;
import org.miradi.dialogs.treetables.GenericTreeTableModel;
import org.miradi.dialogs.treetables.AbstractTreeTablePanel;
import org.miradi.dialogs.treetables.TreeTableWithStateSaving;
import org.miradi.main.CommandExecutedEvent;
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.BaseObject;
import org.miradi.utils.MultiTableRowHeightController;
import org.miradi.utils.MultiTableSelectionController;
import org.miradi.utils.MultiTableVerticalScrollController;
import org.miradi.views.umbrella.PersistentHorizontalSplitPane;
import org.miradi.views.umbrella.PersistentNonPercentageHorizontalSplitPane;

abstract public class AbstractEditableTreeTablePanel extends AbstractTreeTablePanel
{
	public AbstractEditableTreeTablePanel(MainWindow mainWindowToUse, GenericTreeTableModel modelToUse, TreeTableWithStateSaving treeTable, BaseObject baseObject) throws Exception
	{
		this(mainWindowToUse, modelToUse, treeTable, baseObject, new Class[0]);
	}
	
	public AbstractEditableTreeTablePanel(MainWindow mainWindowToUse, GenericTreeTableModel modelToUse, TreeTableWithStateSaving treeTable, BaseObject baseObject, Class[] buttonActionClasses) throws Exception
	{
		// FIXME medium: Much duplicated code with PlanningTreeTablePanel
		super(mainWindowToUse, treeTable, buttonActionClasses);

		model = modelToUse;

		masterVerticalScrollBar = new MasterVerticalScrollBar(treeTableScrollPane);
		treeTableScrollPane.addMouseWheelListener(new MouseWheelHandler(masterVerticalScrollBar));

		rowHeightController = new MultiTableRowHeightController(getMainWindow());
		rowHeightController.addTable(treeTable);

		selectionController = new MultiTableSelectionController(this);
		selectionController.addTable(treeTable);

		scrollController = new MultiTableVerticalScrollController();
		scrollController.addScrollPane(treeTableScrollPane);

		listenForColumnWidthChanges(getTree());

		editableTableModel = createEditableTableModel(mainWindowToUse, treeTable, baseObject);
		editableTable = createEditableTable(mainWindowToUse);
		mainTableScrollPane = integrateTable(masterVerticalScrollBar, scrollController, rowHeightController, selectionController, treeTable, getEditableTable());

		treesPanel = new ShrinkToFitVerticallyHorizontalBox();
		treesPanel.add(treeTableScrollPane);
		treesScrollPane = new ScrollPaneWithHideableScrollBar(treesPanel);
		treesScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		treesScrollPane.hideVerticalScrollBar();

		tablesPanel = new ShrinkToFitVerticallyHorizontalBox();
		ScrollPaneWithHideableScrollBar tablesScrollPane = new ScrollPaneWithHideableScrollBar(tablesPanel);
		tablesScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		tablesScrollPane.hideVerticalScrollBar();

		scrollController.addScrollBar(masterVerticalScrollBar);

		treePlusTablesPanel = new PersistentNonPercentageHorizontalSplitPane(this, mainWindowToUse, getDividerName());
		treePlusTablesPanel.setDividerSize(5);
		// FIXME medium: Remove this when persistence actually works!
		configureSplitter(tablesScrollPane);

		// NOTE: Replace treeScrollPane that super constructor put in CENTER
		add(treePlusTablesPanel, BorderLayout.CENTER);
		add(masterVerticalScrollBar, BorderLayout.AFTER_LINE_ENDS);

		rebuildEntireTreeTable();
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		editableTable.dispose();
	}

	private void configureSplitter(ScrollPaneWithHideableScrollBar tablesScrollPane)
	{
		treePlusTablesPanel.setTopComponent(treesScrollPane);
		treePlusTablesPanel.setBottomComponent(tablesScrollPane);
		treePlusTablesPanel.setOneTouchExpandable(false);
	}

	private void rebuildEntireTreeTable() throws Exception
	{
		ORef selectedRef = ORef.INVALID;
		BaseObject[] selected = tree.getSelectedObjects();
		if(selected.length == 1)
			selectedRef = selected[0].getRef();
		ORefList refHierarchy = getTree().findHierarchyForRef(selectedRef);
		int selectedRow = getTree().getSelectionModel().getMinSelectionIndex();
		
		getTree().rebuildTableCompletely();
		getEditableSingleBooleanColumnTableModel().fireTableStructureChanged();
		
		// NOTE: The following rebuild the tree but don't touch the columns
		getTreeTableModel().rebuildEntireTree();
		restoreTreeExpansionState();
		updateRightSideTablePanels();

		getTree().selectObjectAfterSwingClearsItDueToTreeStructureChange(refHierarchy, selectedRow);
	}

	private EditableObjectTable getEditableTable()
	{
		return editableTable;
	}
	
	protected SingleBooleanColumnEditableModel getEditableSingleBooleanColumnTableModel()
	{
		return editableTableModel;
	}
	
	protected GenericTreeTableModel getTreeTableModel()
	{
		return getModel();
	}
	
	private void updateRightSideTablePanels() throws Exception
	{
		tablesPanel.removeAll();
		tablesPanel.add(mainTableScrollPane);
		
		validate();
		repaint();
	}
	
	@Override
	public void commandExecuted(CommandExecutedEvent event)
	{
		if(event.isSetDataCommand())
			editableTable.repaint();
	}
	
	abstract protected SingleBooleanColumnEditableModel createEditableTableModel(MainWindow mainWindowToUse, TreeTableWithStateSaving treeTable, BaseObject baseObject);
	
	abstract protected EditableObjectTable createEditableTable(MainWindow mainWindowToUse);
	
	abstract protected String getDividerName();
	
	private SingleBooleanColumnEditableModel editableTableModel;
	private EditableObjectTable editableTable;
	private JScrollBar masterVerticalScrollBar;
	private PersistentHorizontalSplitPane treePlusTablesPanel;
	private JPanel treesPanel;
	private JPanel tablesPanel;
	
	private ScrollPaneWithHideableScrollBar treesScrollPane;
	private ScrollPaneWithHideableScrollBar mainTableScrollPane;
	
	private MultiTableSelectionController selectionController;
	private MultiTableRowHeightController rowHeightController;
	private MultiTableVerticalScrollController scrollController;
}
