/* 
Copyright 2005-2009, Foundations of Success, Bethesda, Maryland 
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
package org.miradi.questions;

import java.util.Vector;

import org.miradi.main.EAM;
import org.miradi.objects.Cause;
import org.miradi.project.Project;
import org.miradi.schemas.GoalSchema;
import org.miradi.schemas.HumanWelfareTargetSchema;
import org.miradi.schemas.IndicatorSchema;
import org.miradi.schemas.ObjectiveSchema;
import org.miradi.schemas.StrategySchema;
import org.miradi.schemas.TargetSchema;
import org.miradi.schemas.TaskSchema;

public class PlanningViewSingleLevelQuestion extends ProjectBasedDynamicQuestion
{
	public PlanningViewSingleLevelQuestion(Project projectToUse)
	{
		super();
		
		project = projectToUse;
	}

	private Vector<ChoiceItem> getSingleLevelChoices()
	{
		Vector<ChoiceItem> choices = new Vector<ChoiceItem>();

		choices.add(new ChoiceItem(GoalSchema.OBJECT_NAME, EAM.text("Goals Only")));
		choices.add(new ChoiceItem(ObjectiveSchema.OBJECT_NAME, EAM.text("Objectives Only")));
		choices.add(new ChoiceItem(TargetSchema.OBJECT_NAME, EAM.text("Targets Only")));
		
		if (getProject().getMetadata().isHumanWelfareTargetMode())
			choices.add(new ChoiceItem(HumanWelfareTargetSchema.OBJECT_NAME, EAM.text("Human Wellbeing Targets Only")));
		
		choices.add(new ChoiceItem(Cause.OBJECT_NAME_THREAT, EAM.text("Direct Threats Only")));
		choices.add(new ChoiceItem(StrategySchema.OBJECT_NAME, EAM.text("Strategies Only")));
		choices.add(new ChoiceItem(TaskSchema.ACTIVITY_NAME, EAM.text("Activities Only")));
		choices.add(new ChoiceItem(IndicatorSchema.OBJECT_NAME, EAM.text("Indicators Only")));
		choices.add(new ChoiceItem(TaskSchema.METHOD_NAME, EAM.text("Methods Only")));

		return choices;
	}

	@Override
	public ChoiceItem[] getChoices()
	{
		return getSingleLevelChoices().toArray(new ChoiceItem[0]);
	}
	
	private Project getProject()
	{
		return project;
	}
	
	private Project project;
}
