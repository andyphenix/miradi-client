/* 
Copyright 2005-2014, Foundations of Success, Bethesda, Maryland
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
package org.miradi.dialogs.stress;

import org.miradi.dialogs.base.ObjectPoolTableModel;
import org.miradi.objects.Stress;
import org.miradi.project.Project;
import org.miradi.questions.ChoiceQuestion;
import org.miradi.questions.StressRatingChoiceQuestion;
import org.miradi.questions.StressScopeChoiceQuestion;
import org.miradi.questions.StressSeverityChoiceQuestion;
import org.miradi.schemas.StressSchema;

public class StressPoolTableModel extends ObjectPoolTableModel
{
	public StressPoolTableModel(Project projectToUse)
	{
		super(projectToUse, StressSchema.getObjectType(), getStressColumnTags(projectToUse));
	}
	
	@Override
	public boolean isPseudoFieldColumn(int column)
	{
		String columnTag = getColumnTag(column);
		if (columnTag.equals(Stress.PSEUDO_STRESS_RATING))
			return true;
		
		return false;
	}

	@Override
	public ChoiceQuestion getColumnQuestion(int column)
	{
		return createQuestionForTag(getColumnTag(column));
	}

	public static ChoiceQuestion createQuestionForTag(String columnTag)
	{
		if (columnTag.equals(Stress.TAG_SCOPE))
			return new StressScopeChoiceQuestion();
		
		if (columnTag.equals(Stress.TAG_SEVERITY))
			return new StressSeverityChoiceQuestion();
		
		if (columnTag.equals(Stress.PSEUDO_STRESS_RATING))
			return new StressRatingChoiceQuestion();
		
		return null;
	}
	
	public static String[] getStressColumnTags(Project projectToUse)
	{
		if (projectToUse.isStressBaseMode())
			return STRESS_BASED_COLUMN_TAGS;
		
		return SIMPLE_MODE_COLUMN_TAGS;
	}
	
	@Override
	public String getUniqueTableModelIdentifier()
	{
		return UNIQUE_MODEL_IDENTIFIER;
	}
	
	private static final String UNIQUE_MODEL_IDENTIFIER = "StressPoolTableModel";
	
	private static final String[] STRESS_BASED_COLUMN_TAGS = new String[] {
		Stress.TAG_SHORT_LABEL, 
		Stress.TAG_LABEL,
		Stress.TAG_SCOPE, 
		Stress.TAG_SEVERITY, 
		Stress.PSEUDO_STRESS_RATING,
		Stress.TAG_DETAIL,
	};
	
	private static final String[] SIMPLE_MODE_COLUMN_TAGS = new String[] {
			Stress.TAG_SHORT_LABEL, 
			Stress.TAG_LABEL,
			Stress.TAG_DETAIL,
		};
}
