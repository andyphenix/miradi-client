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

package org.miradi.dialogs.dashboard;

import org.miradi.dialogs.base.ObjectDataInputPanel;
import org.miradi.objecthelpers.ORef;
import org.miradi.objects.Dashboard;
import org.miradi.project.Project;
import org.miradi.questions.ChoiceQuestion;
import org.miradi.questions.DashboardFlagsQuestion;
import org.miradi.questions.OpenStandardsProgressStatusQuestion;
import org.miradi.questions.StaticQuestionManager;

public class DashboardProgressPanel extends ObjectDataInputPanel
{
	public DashboardProgressPanel(Project projectToUse, ORef refToUse, String mapCodeToUse) throws Exception
	{
		super(projectToUse, refToUse);
		
		ChoiceQuestion question = StaticQuestionManager.getQuestion(OpenStandardsProgressStatusQuestion.class);
		addField(createDashboardProgressEditorField(refToUse, Dashboard.TAG_PROGRESS_CHOICE_MAP, question, mapCodeToUse));
		addField(createStringMapField(refToUse, Dashboard.TAG_COMMENTS_MAP, mapCodeToUse));
		addField(createStringCodeListField(refToUse, Dashboard.TAG_FLAGS_MAP, mapCodeToUse, StaticQuestionManager.getQuestion(DashboardFlagsQuestion.class)));
		
		updateFieldsFromProject();
	}

	@Override
	public String getPanelDescription()
	{
		return "OpenStandardsProgessPanel";
	}
}
