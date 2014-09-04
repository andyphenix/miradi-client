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
package org.miradi.forms.summary;

import org.miradi.forms.FieldPanelSpec;
import org.miradi.objects.WwfProjectData;
import org.miradi.questions.StaticQuestionManager;
import org.miradi.questions.WwfEcoRegionsQuestion;
import org.miradi.questions.WwfManagingOfficesQuestion;
import org.miradi.questions.WwfRegionsQuestion;
import org.miradi.schemas.WwfProjectDataSchema;
import org.miradi.views.summary.WWFSummaryPanel;

public class WwfTabForm extends FieldPanelSpec
{
	public WwfTabForm()
	{
		setTranslatedTitle(WWFSummaryPanel.getWwfPanelDescription());

		addCodeListField(WwfProjectDataSchema.getObjectType(), WwfProjectData.TAG_MANAGING_OFFICES, StaticQuestionManager.getQuestion(WwfManagingOfficesQuestion.class));
		addCodeListField(WwfProjectDataSchema.getObjectType(), WwfProjectData.TAG_REGIONS, StaticQuestionManager.getQuestion(WwfRegionsQuestion.class));
		addCodeListField(WwfProjectDataSchema.getObjectType(), WwfProjectData.TAG_ECOREGIONS, StaticQuestionManager.getQuestion(WwfEcoRegionsQuestion.class));		
	}
}
