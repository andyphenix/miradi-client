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

import org.miradi.main.MainWindow;
import org.miradi.utils.ColumnSequenceSaver;
import org.miradi.utils.ColumnWidthProvider;
import org.miradi.utils.ColumnWidthSaver;
import org.miradi.utils.TableWithColumnManagement;
import org.miradi.utils.TableWithColumnWidthAndSequenceSaver;

abstract public class TreeTableWithColumnWidthSaving extends TreeTableWithRowHeightSaver implements TableWithColumnManagement, ColumnWidthProvider
{
	public TreeTableWithColumnWidthSaving(MainWindow mainWindowToUse, GenericTreeTableModel treeTableModel)
	{
		super(mainWindowToUse, treeTableModel);
		
		if (shouldSaveColumnWidth())
			addColumnWidthSaver(mainWindowToUse);
		
		if (shouldSaveColumnSequence())
			addColumnSequenceSaver(mainWindowToUse);
	}

	private void addColumnSequenceSaver(MainWindow mainWindowToUse)
	{
		columnSequenceSaver = new ColumnSequenceSaver(mainWindowToUse.getProject(), this, getUniqueTableIdentifier());
		getTableHeader().addMouseListener(columnSequenceSaver);
	}

	private void addColumnWidthSaver(MainWindow mainWindowToUse)
	{
		columnWidthSaver = new ColumnWidthSaver(mainWindowToUse.getProject(), this, this, getUniqueTableIdentifier());
		getTableHeader().addMouseListener(columnWidthSaver);
	}
	
	public String getColumnGroupCode(int tableColumn)
	{
		final int modelColumnIndex = convertColumnIndexToModel(tableColumn);
		return getTreeTableModel().getColumnTag(modelColumnIndex);
	}
	
	@Override
	public void rebuildTableCompletely() throws Exception
	{
		super.rebuildTableCompletely();
		
		if (shouldSaveColumnWidth()) 
			columnWidthSaver.restoreColumnWidths();
		
		if (shouldSaveColumnSequence())
			columnSequenceSaver.restoreColumnSequences();
	}
	
	public boolean shouldSaveColumnWidth()
	{
		return true;
	}
	
	public boolean shouldSaveColumnSequence()
	{
		return true;
	}
	
	public int getDefaultColumnWidth(int tableColumn, String columnTag, int columnHeaderWidth)
	{
		if (columnTag.equals(GenericTreeTableModel.DEFAULT_COLUMN))
			return ColumnWidthSaver.DEFAULT_WIDE_COLUMN_WIDTH;
		
		else if (columnHeaderWidth < ColumnWidthSaver.DEFAULT_NARROW_COLUMN_WIDTH)
			return ColumnWidthSaver.DEFAULT_NARROW_COLUMN_WIDTH;
		
		return columnHeaderWidth;
	}
	
	public String getColumnIdentifier(int tableColumn)
	{
		return TableWithColumnWidthAndSequenceSaver.createColumnIdentifier(this, this, getTreeTableModel(), tableColumn);
	}
		
	private ColumnWidthSaver columnWidthSaver;
	private ColumnSequenceSaver columnSequenceSaver;
}
