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
package org.miradi.dialogs.treeRelevancy;

import org.miradi.dialogs.treetables.GenericTreeTableModel;
import org.miradi.dialogs.treetables.TreeTableNode;
import org.miradi.main.EAM;

public class StrategyActivityRelevancyTreeTableModel extends GenericTreeTableModel
{
	public StrategyActivityRelevancyTreeTableModel(TreeTableNode root)
	{
		super(root);
	}

	public String getColumnTag(int column)
	{
		return "";
	}

	public int getColumnCount()
	{
		return COLUMN_COUNT;
	}

	public String getColumnName(int column)
	{
		return COLUMN_NAME;
	}
	
	public static final String COLUMN_NAME = EAM.text("Item");
	public static final int COLUMN_COUNT = 1;
}
