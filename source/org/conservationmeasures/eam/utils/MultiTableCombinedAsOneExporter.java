/* 
* Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
* (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.utils;

import java.util.Vector;

public class MultiTableCombinedAsOneExporter implements ExportableTableInterface
{
	public MultiTableCombinedAsOneExporter()
	{
		clear();
	}

	public void clear()
	{
		tables = new Vector();
	}
	
	public void addTable(ExportableTableInterface table)
	{
		tables.add(table);
	}
	
	public int getColumnCount()
	{
		int combinedCount = 0;
		for (int i = 0;  i < tables.size(); ++i)
		{
			combinedCount += tables.get(i).getColumnCount();
		}
		
		return combinedCount;
	}

	public int getDepth(int row)
	{
		int depth = 0;
		for (int i = 0;  i < tables.size(); ++i)
		{
			depth = Math.max(depth, tables.get(i).getDepth(row));
		}
		
		return depth;
	}

	//FIXME the below 2 methods are almost identical.  
	//need to refactor out common code
	public String getHeaderFor(int column)
	{
		int validIndex = column;
		int thisColumnCount = 0;
		for (int i = 0; i < tables.size(); ++i)
		{
			thisColumnCount += tables.get(i).getColumnCount();
			if (thisColumnCount <= column)
			{
				validIndex -= tables.get(i).getColumnCount();
				continue;
			}
						
			return tables.get(i).getHeaderFor(validIndex);
		}
		
		throw new RuntimeException("Error occurred while exporting table.");
	}
	
	private String getTableForColumn(int row, int column)
	{
		int validIndex = column;
		int thisColumnCount = 0;
		for (int i = 0; i < tables.size(); ++i)
		{
			thisColumnCount += tables.get(i).getColumnCount();
			if (thisColumnCount <= column)
			{
				validIndex -= tables.get(i).getColumnCount();
				continue;
			}
						
			return tables.get(i).getValueFor(row, validIndex);
		}
		
		throw new RuntimeException("Error occurred while exporting table.");
	}

	public int getMaxDepthCount()
	{
		int maxDepth = 0;
		for (int i = 0;  i < tables.size(); ++i)
		{
			maxDepth = Math.max(maxDepth, tables.get(i).getMaxDepthCount());
		}
		
		return maxDepth;
	}

	public int getRowCount()
	{
		if (tables.size() > 0) 
			return tables.get(0).getRowCount();
		
		return 0;
	}

	public String getValueFor(int row, int column)
	{
		return getTableForColumn(row, column);
	}
	
	private Vector<ExportableTableInterface> tables;
}
