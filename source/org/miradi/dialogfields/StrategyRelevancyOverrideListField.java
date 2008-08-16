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
package org.miradi.dialogfields;

import org.miradi.ids.BaseId;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.Objective;
import org.miradi.project.Project;
import org.miradi.questions.ChoiceQuestion;

public class StrategyRelevancyOverrideListField extends RelevancyOverrideListField
{
	public StrategyRelevancyOverrideListField(Project projectToUse, int objectTypeToUse, BaseId objectIdToUse, ChoiceQuestion questionToUse)
	{
		super(projectToUse, objectTypeToUse, objectIdToUse, questionToUse, Objective.TAG_RELEVANT_STRATEGY_SET);
	}
	
	public String getText()
	{
		try
		{
			Objective objective = Objective.find(getProject(), getORef());
			ORefList all = new ORefList(refListEditor.getText());
			
			return objective.getCalculatedRelevantStrategyrOverrides(all).toString();
		}
		catch(Exception e)
		{
			EAM.logException(e);
			return "ERROR";
		}
	}

	public void setText(String codes)
	{
		try
		{
			Objective objective = Objective.find(getProject(), getORef());
			ORefList relevantRefList = objective.getRelevantStrategyAndActivityRefs();
			refListEditor.setText(relevantRefList.toString());
		}
		catch(Exception e)
		{
			//FIXME do something else with this exception
			EAM.logException(e);
		}	
	}
}
