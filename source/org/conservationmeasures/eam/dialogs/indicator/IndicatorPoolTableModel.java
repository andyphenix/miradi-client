/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs.indicator;

import org.conservationmeasures.eam.dialogs.base.ObjectPoolTableModel;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.Indicator;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.questions.ChoiceQuestion;
import org.conservationmeasures.eam.questions.IndicatorStatusRatingQuestion;
import org.conservationmeasures.eam.questions.PriorityRatingQuestion;


public class IndicatorPoolTableModel extends ObjectPoolTableModel
{
	public IndicatorPoolTableModel(Project projectToUse)
	{
		super(projectToUse, ObjectType.INDICATOR, COLUMN_TAGS);
	}
		
	public ChoiceQuestion getColumnQuestion(int column)
	{
		if (getColumnTag(column).equals(Indicator.TAG_PRIORITY))
			return new PriorityRatingQuestion(getColumnTag(column));
		
		if (getColumnTag(column).equals(Indicator.TAG_STATUS))
			return new IndicatorStatusRatingQuestion(getColumnTag(column));
		
		return null;
	}

	private static final String[] COLUMN_TAGS = new String[] {
		Indicator.TAG_SHORT_LABEL,
		Indicator.TAG_LABEL,
		Indicator.TAG_PRIORITY,
		Indicator.TAG_STATUS,
		Indicator.PSEUDO_TAG_FACTOR,
		Indicator.PSEUDO_TAG_METHODS,
		Indicator.PSEUDO_TAG_TARGETS,
		Indicator.PSEUDO_TAG_DIRECT_THREATS,
	};

}
