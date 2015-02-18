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

package org.miradi.dialogs.planning;

import org.miradi.project.Project;
import org.miradi.questions.WorkPlanColumnConfigurationQuestion;
import org.miradi.schemas.BudgetCategoryOneSchema;
import org.miradi.utils.CodeList;

public class BudgetCategoryOneCoreRowColumnProvider extends AbstractBudgetCategoryRowColumnProvider
{
	public BudgetCategoryOneCoreRowColumnProvider(Project projectToUse)
	{
		super(projectToUse);
	}

	@Override
	public CodeList getColumnCodesToShow()  throws Exception
	{
		CodeList columnCodes = super.getColumnCodesToShow();
		columnCodes.add(WorkPlanColumnConfigurationQuestion.META_BUDGET_CATEGORY_ONE_WORK_UNITS_COLUMN_CODE);
		columnCodes.add(WorkPlanColumnConfigurationQuestion.META_BUDGET_CATEGORY_ONE_EXPENSE_COLUMN_CODE);
		columnCodes.add(WorkPlanColumnConfigurationQuestion.META_BUDGET_CATEGORY_ONE_BUDGET_DETAILS_COLUMN_CODE);
		
		return columnCodes; 
	}
	
	@Override
	public String getObjectTypeName()
	{
		return BudgetCategoryOneSchema.OBJECT_NAME;
	}
	
	@Override
	public int getObjectType()
	{
		return BudgetCategoryOneSchema.getObjectType();
	}
}
