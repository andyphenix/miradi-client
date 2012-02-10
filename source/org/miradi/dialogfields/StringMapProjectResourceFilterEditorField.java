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

package org.miradi.dialogfields;

import java.text.ParseException;

import org.miradi.ids.BaseId;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.CodeToCodeListMap;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.TableSettings;
import org.miradi.project.Project;
import org.miradi.questions.ChoiceQuestion;

public class StringMapProjectResourceFilterEditorField extends RefListEditorField
{
	public StringMapProjectResourceFilterEditorField(Project projectToUse, int objectTypeToUse, BaseId objectIdToUse, String tagToUse, ChoiceQuestion questionToUse)
	{
		super(projectToUse, objectTypeToUse, objectIdToUse, tagToUse, questionToUse);
	}

	@Override
	public String getText()
	{
		try
		{
			return getStringMapAsString();
		}
		catch (Exception e)
		{
			EAM.unexpectedErrorDialog(e);
			EAM.logException(e);
			return "";
		}
	}

	private String getStringMapAsString() throws Exception
	{
		CodeToCodeListMap existingMap = new CodeToCodeListMap(getProject().getObjectData(getORef(), getTag()));
		existingMap.putRefList(TableSettings.WORK_PLAN_PROJECT_RESOURCE_FILTER_CODELIST_KEY, getComponentRefList());
		
		return existingMap.toJsonString();
	}

	@Override
	public void setText(String stringMapAsString)
	{
		ORefList codes = createCodeListFromString(stringMapAsString);
		super.setText(codes.toString());
	}

	private ORefList createCodeListFromString(String StringMapAsString)
	{
		try
		{
			CodeToCodeListMap stringMap = new CodeToCodeListMap(StringMapAsString);
			return stringMap.getRefList(TableSettings.WORK_PLAN_PROJECT_RESOURCE_FILTER_CODELIST_KEY);
		}
		catch(ParseException e)
		{
			EAM.unexpectedErrorDialog(e);
			EAM.logException(e);
			return new ORefList();
		}
	}
}
