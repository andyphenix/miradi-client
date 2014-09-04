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
package org.miradi.forms.objects;

import org.miradi.forms.FieldPanelSpec;
import org.miradi.main.EAM;
import org.miradi.objects.Desire;
import org.miradi.objects.Goal;
import org.miradi.schemas.GoalSchema;

public class GoalPropertiesForm extends FieldPanelSpec
{
	public GoalPropertiesForm()
	{
		addLabelAndFieldsWithLabels(EAM.text("Goal"), GoalSchema.getObjectType(), new String[]{Goal.TAG_SHORT_LABEL, Goal.TAG_LABEL});
		addLabelAndField(GoalSchema.getObjectType(), Desire.TAG_FULL_TEXT);
		
		addLabelAndField(GoalSchema.getObjectType(), Desire.PSEUDO_TAG_FACTOR);
		addLabelAndField(GoalSchema.getObjectType(), Desire.PSEUDO_TAG_DIRECT_THREATS);
		addLabelAndField(GoalSchema.getObjectType(), Desire.TAG_COMMENTS);
		addMultipleTaxonomyWithEditButtonFields(GoalSchema.getObjectType(), Goal.TAG_TAXONOMY_CLASSIFICATION_CONTAINER);
	}
}
