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

package org.miradi.dialogs.planning.upperPanel;

import org.miradi.dialogs.tablerenderers.RowColumnBaseObjectProvider;
import org.miradi.objects.PlanningTreeRowColumnProvider;
import org.miradi.project.Project;

public class BudgetCategoryTwoExpenseTableModel extends	CategorizedExpenseAmountsTableModel
{
	public BudgetCategoryTwoExpenseTableModel(Project projectToUse, PlanningTreeRowColumnProvider rowColumnProvider, RowColumnBaseObjectProvider providerToUse, String treeModelIdentifierAsTagToUse) throws Exception
	{
		super(projectToUse, rowColumnProvider, providerToUse, treeModelIdentifierAsTagToUse);
	}
	
	@Override
	public String getUniqueTableModelIdentifier()
	{
		return getTreeModelIdentifierAsTag() + "." + UNIQUE_TABLE_MODEL_IDENTIFIER;
	}
	
	private static final String UNIQUE_TABLE_MODEL_IDENTIFIER = "BudgetCategoryTwoExpenseTableModel";
}