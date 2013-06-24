/* 
Copyright 2005-2013, Foundations of Success, Bethesda, Maryland 
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

package org.miradi.objecthelpers;

import java.util.Comparator;

import org.miradi.dialogs.treetables.TreeTableNode;
import org.miradi.utils.BaseObjectDateDescendingAndIdComparator;

public class BaseObjectByTagSorter implements Comparator<TreeTableNode>
{
	public BaseObjectByTagSorter(String tagToSortByToUse)
	{
		tagToSortBy = tagToSortByToUse;
	}
	
	public int compare(TreeTableNode rawNode1, TreeTableNode rawNode2)
	{
		return BaseObjectDateDescendingAndIdComparator.compare(rawNode1.getObject(), rawNode2.getObject(), getTagToSortBy());
	}
	
	private String getTagToSortBy()
	{
		return tagToSortBy;
	}
	
	private String tagToSortBy;
}