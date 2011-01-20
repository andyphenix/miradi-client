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

package org.miradi.dialogfields;

import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.RelevancyOverrideSet;
import org.miradi.objects.Strategy;
import org.miradi.project.Project;
import org.miradi.questions.ChoiceQuestion;

public class GoalRelevancyOverrideListField extends AbstractRelevancyOverrideListField
{
	public GoalRelevancyOverrideListField(Project projectToUse, ORef refToUse, String tagToUse, ChoiceQuestion questionToUse)
	{
		super(projectToUse, refToUse, tagToUse, questionToUse);
	}

	@Override
	protected RelevancyOverrideSet getCalculatedRelevantOverrides(Strategy strategy, ORefList all) throws Exception
	{
		return strategy.getCalculatedRelevantGoalOverrides(all);
	}

	@Override
	protected ORefList getRelevantRefs(Strategy strategy) throws Exception
	{
		return strategy.getRelevantGoalRefs();
	}
}
