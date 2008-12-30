/* 
Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
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
import org.miradi.utils.ColumnWidthSaver;

abstract public class TreeTableWithColumnWidthSaving extends TreeTableWithRowHeightSaver
{
	public TreeTableWithColumnWidthSaving(MainWindow mainWindowToUse, GenericTreeTableModel treeTableModel)
	{
		super(mainWindowToUse, treeTableModel);
		
		if (shouldSaveColumnWidth())
			addColumnWidthSaver(mainWindowToUse, treeTableModel);
		
		if (shouldSaveColumnSequence())
			addColumnSequenceSaver(mainWindowToUse, treeTableModel);
	}

	private void addColumnSequenceSaver(MainWindow mainWindowToUse, GenericTreeTableModel treeTableModel)
	{
		columnSequenceSaver = new ColumnSequenceSaver(mainWindowToUse.getProject(), this, treeTableModel, getUniqueTableIdentifier());
		getTableHeader().addMouseListener(columnSequenceSaver);
	}

	private void addColumnWidthSaver(MainWindow mainWindowToUse, GenericTreeTableModel treeTableModel)
	{
		columnWidthSaver = new ColumnWidthSaver(mainWindowToUse.getProject(), this, treeTableModel, getUniqueTableIdentifier());
		getTableHeader().addMouseListener(columnWidthSaver);
	}
	
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
		
	private ColumnWidthSaver columnWidthSaver;
	private ColumnSequenceSaver columnSequenceSaver;
}
