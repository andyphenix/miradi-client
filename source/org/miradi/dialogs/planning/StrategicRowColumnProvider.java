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
package org.miradi.dialogs.planning;

import org.miradi.objects.BaseObject;
import org.miradi.objects.ConceptualModelDiagram;
import org.miradi.objects.Desire;
import org.miradi.objects.Goal;
import org.miradi.objects.Objective;
import org.miradi.objects.ResultsChainDiagram;
import org.miradi.objects.Strategy;
import org.miradi.objects.Target;
import org.miradi.questions.CustomPlanningColumnsQuestion;
import org.miradi.utils.CodeList;

public class StrategicRowColumnProvider implements RowColumnProvider
{	
	public CodeList getColumnCodesToShow()
	{
		return new CodeList(new String[] {
				CustomPlanningColumnsQuestion.META_CURRENT_RATING,
				CustomPlanningColumnsQuestion.META_WHO_TOTAL,
				BaseObject.PSEUDO_TAG_LATEST_PROGRESS_REPORT_CODE,
				Desire.TAG_FULL_TEXT,
				});
	}

	public CodeList getRowCodesToShow()
	{
		return new CodeList(new String[] {
				ResultsChainDiagram.OBJECT_NAME,
				ConceptualModelDiagram.OBJECT_NAME,
				Target.OBJECT_NAME,
				Goal.OBJECT_NAME,
				Objective.OBJECT_NAME,
				Strategy.OBJECT_NAME,});
	}

	public CodeList getFlippedRowListToShow()
	{
		return new CodeList(new String[] {
				ResultsChainDiagram.OBJECT_NAME,
				ConceptualModelDiagram.OBJECT_NAME,
				Target.OBJECT_NAME,
				Strategy.OBJECT_NAME,
				Goal.OBJECT_NAME,
				Objective.OBJECT_NAME,
		});
	}
}
