/* 
Copyright 2005-2011, Foundations of Success, Bethesda, Maryland 
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

package org.miradi.dialogs.planning;

import org.miradi.objects.BaseObject;
import org.miradi.objects.ConceptualModelDiagram;
import org.miradi.objects.Desire;
import org.miradi.objects.ResultsChainDiagram;
import org.miradi.objects.Strategy;
import org.miradi.objects.Target;
import org.miradi.project.Project;
import org.miradi.questions.CustomPlanningColumnsQuestion;
import org.miradi.schemas.GoalSchema;
import org.miradi.schemas.ObjectiveSchema;
import org.miradi.utils.CodeList;

public class ActionPlanSubViewStrategyBasedRowColumnProvider extends AbstractPlanningTreeRowColumnProvider
{
	public ActionPlanSubViewStrategyBasedRowColumnProvider(Project projectToUse)
	{
		super(projectToUse);
	}

	public CodeList getColumnCodesToShow() throws Exception
	{
		return new CodeList(new String[] {
				CustomPlanningColumnsQuestion.META_CURRENT_RATING,
				CustomPlanningColumnsQuestion.META_WHO_TOTAL,
				BaseObject.PSEUDO_TAG_LATEST_PROGRESS_REPORT_CODE,
				Desire.TAG_FULL_TEXT,
				});
	}

	public CodeList getRowCodesToShow() throws Exception
	{
		return new CodeList(new String[] {
		ResultsChainDiagram.OBJECT_NAME,
		ConceptualModelDiagram.OBJECT_NAME,
		Target.OBJECT_NAME,
		GoalSchema.OBJECT_NAME,
		ObjectiveSchema.OBJECT_NAME,
		Strategy.OBJECT_NAME,
		});
	}
	
	public boolean shouldIncludeResultsChain()
	{
		return true;
	}

	public boolean shouldIncludeConceptualModelPage()
	{
		return true;
	}
	
	@Override
	public boolean doObjectivesContainStrategies() throws Exception
	{
		return false;
	}
}
