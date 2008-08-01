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
package org.miradi.utils;

import javax.swing.JTable;

import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.BaseObject;

public class TableExporter extends AbstractTableExporter
{
	public TableExporter(JTable tableToUse)
	{
		tableToExport = tableToUse;
	}

	@Override
	public int getRowCount()
	{
		return tableToExport.getRowCount();
	}

	@Override
	public int getColumnCount()
	{
		return tableToExport.getColumnCount();
	}

	@Override
	public int getDepth(int row)
	{
		return 0;
	}

	@Override
	public String getHeaderFor(int column)
	{
		return tableToExport.getColumnName(column);
	}

	@Override
	public String getIconAt(int row, int column)
	{
		return null;
	}

	@Override
	public int getMaxDepthCount()
	{
		return 0;
	}
	
	@Override
	public BaseObject getBaseObjectForRow(int row)
	{
		return null;
	}
	
	@Override
	public int getRowType(int row)
	{
		return ObjectType.FAKE;
	}

	@Override
	public String getTextAt(int row, int column)
	{
		Object value = tableToExport.getValueAt(row, column);
		return getSafeValue(value);
	}
		
	private JTable tableToExport;
}
