/* 
Copyright 2005-2010, Foundations of Success, Bethesda, Maryland 
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

package org.miradi.dialogs.base;

import java.text.ParseException;
import java.util.Vector;

import org.miradi.dialogfields.AbstractStringStringMapEditorField;
import org.miradi.dialogfields.QuestionBasedEditorComponent;
import org.miradi.dialogfields.RadioButtonEditorComponent;
import org.miradi.dialogs.dashboard.DashboardRowDefinition;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.AbstractStringKeyMap;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.CodeToChoiceMap;
import org.miradi.objects.Dashboard;
import org.miradi.project.Project;
import org.miradi.questions.ChoiceItem;
import org.miradi.questions.ChoiceQuestion;
import org.miradi.questions.OpenStandardsProgressStatusQuestion;
import org.miradi.utils.CodeList;

public class DashboardProgressEditorField extends AbstractStringStringMapEditorField
{
	public DashboardProgressEditorField(Project projectToUse, ORef refToUse, String tagToUse, ChoiceQuestion questionToUse, String mapCodeToUse)
	{
		super(projectToUse, refToUse, tagToUse, questionToUse, mapCodeToUse);
	}
	
	@Override
	protected QuestionBasedEditorComponent createCodeListEditor(ChoiceQuestion questionToUse, int columnCount)
	{
		return new RadioButtonEditorComponent(questionToUse);
	}
	
	@Override
	public void updateEditableState()
	{
		super.updateEditableState();

		try
		{
			boolean canBeCalculatedFromUserData = false;
			ORef dashboardRef = getProject().getSingletonObjectRef(Dashboard.getObjectType());
			Dashboard dashboard = Dashboard.find(getProject(), dashboardRef);
			Vector<DashboardRowDefinition> rows = dashboard.getDashboardRowDefinitionManager().getRowDefinitions(getMapCode());
			if(rows.size() > 0)
				canBeCalculatedFromUserData = true;
			
			String calculatedState = getProject().getDashboardStatusMapsCache().getCalculatedStatusMap().get(getMapCode());

			setRadioButtonEnabled(OpenStandardsProgressStatusQuestion.NOT_APPLICABLE_CODE, true);

			if(!canBeCalculatedFromUserData)
			{
				setRadioButtonEnabled(OpenStandardsProgressStatusQuestion.NOT_SPECIFIED_CODE, false);
				setRadioButtonEnabled(OpenStandardsProgressStatusQuestion.NOT_STARTED_CODE, true);
				setRadioButtonEnabled(OpenStandardsProgressStatusQuestion.IN_PROGRESS_CODE, true);
				setRadioButtonEnabled(OpenStandardsProgressStatusQuestion.COMPLETE_CODE, true);
			}
			else if(calculatedState.equals(OpenStandardsProgressStatusQuestion.NOT_STARTED_CODE))
			{
				setRadioButtonEnabled(OpenStandardsProgressStatusQuestion.NOT_SPECIFIED_CODE, true);
				setRadioButtonEnabled(OpenStandardsProgressStatusQuestion.NOT_STARTED_CODE, true);
				setRadioButtonEnabled(OpenStandardsProgressStatusQuestion.IN_PROGRESS_CODE, false);
				setRadioButtonEnabled(OpenStandardsProgressStatusQuestion.COMPLETE_CODE, false);
			}
			else
			{
				setRadioButtonEnabled(OpenStandardsProgressStatusQuestion.NOT_SPECIFIED_CODE, true);
				setRadioButtonEnabled(OpenStandardsProgressStatusQuestion.NOT_STARTED_CODE, false);
				setRadioButtonEnabled(OpenStandardsProgressStatusQuestion.IN_PROGRESS_CODE, true);
				setRadioButtonEnabled(OpenStandardsProgressStatusQuestion.COMPLETE_CODE, true);
			}
		}
		catch(Exception e)
		{
			EAM.logException(e);
			setEditable(false);
		}
	}

	private void setRadioButtonEnabled(String questionCode, boolean shouldBeEnabled)
	{
		RadioButtonEditorComponent radioButtons = (RadioButtonEditorComponent) getComponent();
		ChoiceQuestion question = getProject().getQuestion(OpenStandardsProgressStatusQuestion.class);
		ChoiceItem choice = question.findChoiceByCode(questionCode);
		radioButtons.setSingleButtonEnabled(choice, shouldBeEnabled);
	}
	
	@Override
	public String getText()
	{
		try
		{
			AbstractStringKeyMap existingMap = new CodeToChoiceMap(getProject().getObjectData(getORef(), getTag()));
			CodeList codes = new CodeList(super.getText());
			if (!codes.isEmpty())
				existingMap.put(getMapCode(), codes.firstElement());
			
			return existingMap.toString();
		}
		catch(ParseException e)
		{
			EAM.logException(e);
			EAM.unexpectedErrorDialog(e);
		}
		
		return "";
	}

	@Override
	public void setText(String stringMapAsString)
	{
		try
		{
			AbstractStringKeyMap stringChoiceMap = new CodeToChoiceMap(stringMapAsString);
			String code = stringChoiceMap.get(getMapCode());
			super.setText(code);
		}
		catch(ParseException e)
		{
			EAM.unexpectedErrorDialog(e);
			EAM.logException(e);
		}
	}
}
