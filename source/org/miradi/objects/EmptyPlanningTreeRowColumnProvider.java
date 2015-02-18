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

package org.miradi.objects;

import org.miradi.questions.WorkPlanVisibleRowsQuestion;
import org.miradi.utils.CodeList;

public class EmptyPlanningTreeRowColumnProvider implements PlanningTreeRowColumnProvider
{
	public CodeList getRowCodesToShow() throws Exception
	{
		throw new RuntimeException("This method should never be called");
	}

	public CodeList getColumnCodesToShow() throws Exception
	{
		throw new RuntimeException("This method should never be called");
	}

	public boolean shouldIncludeResultsChain() throws Exception
	{
		throw new RuntimeException("This method should never be called");
	}

	public boolean shouldIncludeConceptualModelPage() throws Exception
	{
		throw new RuntimeException("This method should never be called");
	}

	public boolean doObjectivesContainStrategies() throws Exception
	{
		throw new RuntimeException("This method should never be called");
	}

	public boolean shouldPutTargetsAtTopLevelOfTree() throws Exception
	{
		throw new RuntimeException("This method should never be called");
	}

	public String getWorkPlanBudgetMode() throws Exception
	{
		return WorkPlanVisibleRowsQuestion.SHOW_ALL_ROWS_CODE;
	}
}
