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
package org.miradi.objecthelpers;

import java.util.Comparator;

import org.miradi.main.EAM;
import org.miradi.utils.SortableTable;
import org.miradi.wizard.noproject.FileSystemTreeNode;

public class FileSystemProjectSorter implements Comparator<FileSystemTreeNode>
{
	public FileSystemProjectSorter()
	{
		currentSortTag = PROJECT_NAME_SORT_TAG;
		resetColumnSortDirectionToDefault();
	}
	
	public void resortBy(String columnTag)
	{
		if (columnTag.equals(currentSortTag))
		{
			reverseSortDirection();
		}
		else
		{
			setColumnSortTag(columnTag);
			resetColumnSortDirectionToDefault();
		}
	}
	
	public int compare(FileSystemTreeNode node1, FileSystemTreeNode node2)
	{
		try
		{
			int rawComparisonResult = compareWithoutDirection(node1, node2);
			if (isReverseSort())
				return getNegatedValue(rawComparisonResult);
			
			return rawComparisonResult;
		}
		catch(Exception e)
		{
			EAM.logException(e);
			return 0;
		}
	}

	private int compareWithoutDirection(FileSystemTreeNode node1, FileSystemTreeNode node2)throws Exception
	{
		if (!node1.isLegacyProjectDirectory() && node2.isLegacyProjectDirectory())
			return -1;
		
		if (node1.isLegacyProjectDirectory() && !node2.isLegacyProjectDirectory())
			return 1;
		
		return compareByTag(node1, node2);
	}

	private int getNegatedValue(int compareByTag)
	{
		final int NEGATIVE_ONE = -1;
		return compareByTag * NEGATIVE_ONE;
	}

	private int compareByTag(FileSystemTreeNode node1, FileSystemTreeNode node2) throws Exception
	{
		if (currentSortTag.equals(PROJECT_NAME_SORT_TAG))
			return node1.toString().compareToIgnoreCase(node2.toString());
		
		return node1.getLastModifiedDate().compareTo(node2.getLastModifiedDate());
	}
	
	private boolean isReverseSort()
	{
		return isReverseSort(currentSortDirection);
	}
	
	private void setColumnSortTag(String columnSortTagToUse)
	{
		currentSortTag = columnSortTagToUse;
	}

	private void reverseSortDirection()
	{
		if (isReverseSort(currentSortDirection))
			resetColumnSortDirectionToDefault();
		else
			currentSortDirection = SortableTable.REVERSE_SORT_ORDER;
	}

	private void resetColumnSortDirectionToDefault()
	{
		currentSortDirection = SortableTable.DEFAULT_SORT_DIRECTION;
	}
	
	private boolean isReverseSort(int sortDirectionToUse)
	{
		return sortDirectionToUse == SortableTable.REVERSE_SORT_ORDER;
	}
				
	private static final String PROJECT_NAME_SORT_TAG = "Project";
	
	private String currentSortTag;
	private int currentSortDirection;
}
