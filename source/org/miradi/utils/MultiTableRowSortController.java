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
package org.miradi.utils;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.JTableHeader;

import org.miradi.commands.CommandSetObjectData;
import org.miradi.main.CommandExecutedEvent;
import org.miradi.main.CommandExecutedListener;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.TableSettings;
import org.miradi.project.Project;
import org.miradi.questions.SortDirectionQuestion;

public class MultiTableRowSortController implements CommandExecutedListener
{
	public MultiTableRowSortController(Project projectToUse)
	{
		project = projectToUse;
		tablesToSort = new Vector<TableWithRowHeightSaver>();
		
		project.addCommandExecutedListener(this);
	}
	
	public void dispose()
	{
		project.removeCommandExecutedListener(this);
	}
	
	public void addTableToSort(TableWithRowHeightSaver tableToSort) throws Exception
	{
		JTableHeader columnHeader = tableToSort.getTableHeader();
		ColumnSortListener sortListener = new ColumnSortListener();
		columnHeader.addMouseListener(sortListener);
		tablesToSort.add(tableToSort);
		
		sortAllTables();
	}

	private int findColumnToSortBy(SortableTableModel model) throws Exception
	{
		TableSettings tableSettings = findOrCreateTableSettings(model);
		String columnSortTag = tableSettings.getData(TableSettings.TAG_COLUMN_SORT_TAG);
		for (int column = 0; column < model.getColumnCount(); ++column)
		{
			String columnTag = model.getColumnGroupCode(column);
			if (columnTag.equals(columnSortTag))
				return column;
		}
		
		return -1;
	}
	
	public void sortAllTables() throws Exception
	{
		for(JTable tableToSort : tablesToSort)
		{
			sortTable(tableToSort);
		}
	}

	private void sortTable(JTable tableToSort) throws Exception
	{
		SortableTableModel model = getCastedModel(tableToSort);
		final int columnToSort = findColumnToSortBy(model);
		if (columnToSort < 0)
			return;
		
		TableSettings tableSettings = findOrCreateTableSettings(model);
		int modelColumn = tableToSort.convertColumnIndexToModel(columnToSort);
		String sortDirectionCode = tableSettings.getData(TableSettings.TAG_COLUMN_SORT_DIRECTION);
		ORefList sortedRefs = model.getSortedRefs(modelColumn, sortDirectionCode);
		for (int index = 0; index < tablesToSort.size(); ++index)
		{
			TableWithRowHeightSaver table = tablesToSort.get(index);
			SortableTableModel modelToSetThreats = (SortableTableModel)table.getModel();
			modelToSetThreats.setSortedRefs(sortedRefs);
			table.updateAutomaticRowHeights();
			table.revalidate();
			table.repaint();
		}
	}
	
	private TableSettings findOrCreateTableSettings(SortableTableModel model)	throws Exception
	{
		String uniqueTableIdentifier = model.getUniqueTableModelIdentifier();
		return TableSettings.findOrCreate(getProject(), uniqueTableIdentifier);
	}	
	
	private void storeSameDataForAllTables(String columnSortTag, String columnSortDirection) throws Exception
	{
		for (TableWithRowHeightSaver table : tablesToSort)
		{
			SortableTableModel model = getCastedModel(table);
			TableSettings tableSettings = findOrCreateTableSettings(model);
			
			saveUsingCommand(tableSettings, TableSettings.TAG_COLUMN_SORT_TAG, columnSortTag);
			saveUsingCommand(tableSettings, TableSettings.TAG_COLUMN_SORT_DIRECTION, columnSortDirection);
		}
	}
	
	private void saveUsingCommand(TableSettings tableSettings, String tag, String value) throws Exception
	{
		CommandSetObjectData setColumnData = new CommandSetObjectData(tableSettings.getRef(), tag, value);
		getProject().executeCommand(setColumnData);
	}
	
	public void commandExecuted(CommandExecutedEvent event)
	{
		try
		{
			if (event.isSetDataCommandWithThisTypeAndTag(TableSettings.getObjectType(), TableSettings.TAG_COLUMN_SORT_TAG) ||
				event.isSetDataCommandWithThisTypeAndTag(TableSettings.getObjectType(), TableSettings.TAG_COLUMN_SORT_DIRECTION))
			{
				sortAllTables();
			}
		}
		catch (Exception e)
		{
			EAM.logException(e);
			EAM.errorDialog(EAM.text("An Error Occurred During Sorting."));
		}
	}
	
	private SortableTableModel getCastedModel(JTable tableToUse)
	{
		return (SortableTableModel)tableToUse.getModel();
	}

	private Project getProject()
	{
		return project;
	}
	
	private class ColumnSortListener extends MouseAdapter
	{
		@Override
		public void mouseClicked(MouseEvent event) 
		{
			try
			{
				JTableHeader tableHeader = ((JTableHeader)event.getSource());
				int clickedTableColumn = tableHeader.columnAtPoint(event.getPoint());
				if (clickedTableColumn >= 0)
					sortByTableColumn(tableHeader.getTable(), clickedTableColumn);
			}
			catch (Exception e)
			{
				EAM.logException(e);
				EAM.errorDialog(EAM.text("Error Occurred During Sorting"));
			}
		}

		private void sortByTableColumn(JTable tableClickedOn, int sortByTableColumn) throws Exception
		{
			SortableTableModel model = getCastedModel(tableClickedOn);
			TableSettings tableSettings = findOrCreateTableSettings(model);
			String columnSortTag = model.getColumnGroupCode(sortByTableColumn);
			String currentSortDirection = getSortDirectionCode(tableSettings, columnSortTag);
			
			saveColumnSortDataAsTransaction(columnSortTag, currentSortDirection);
		}

		private void saveColumnSortDataAsTransaction(String columnSortTag,	String currentSortDirection) throws Exception
		{
			getProject().executeBeginTransaction();
			try
			{
				storeSameDataForAllTables(columnSortTag, currentSortDirection);
			}
			finally 
			{
				getProject().executeEndTransaction();
			}
		}

		private String getSortDirectionCode(TableSettings tableSettings, String columnSortTag)
		{
			String existingSortDirection = tableSettings.getData(TableSettings.TAG_COLUMN_SORT_DIRECTION);
			String existingSortTag = tableSettings.getData(TableSettings.TAG_COLUMN_SORT_TAG);
			if (existingSortTag.equals(columnSortTag))
				return SortDirectionQuestion.getReversedSortDirectionCode(existingSortDirection);
			
			return SortDirectionQuestion.DEFAULT_SORT_ORDER_CODE;
		}
	}
	
	private Vector<TableWithRowHeightSaver> tablesToSort;
	private Project project;
}
