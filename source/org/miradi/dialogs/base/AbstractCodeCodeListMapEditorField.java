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

import org.miradi.dialogfields.AbstractStringStringMapEditorField;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.AbstractStringToStringMap;
import org.miradi.objecthelpers.ORef;
import org.miradi.project.Project;
import org.miradi.questions.ChoiceQuestion;
import org.miradi.utils.CodeList;

abstract public class AbstractCodeCodeListMapEditorField extends AbstractStringStringMapEditorField
{
	public AbstractCodeCodeListMapEditorField(Project projectToUse, ORef refToUse, String tagToUse, ChoiceQuestion questionToUse, String mapCodeToUse)
	{
		super(projectToUse, refToUse, tagToUse, questionToUse, mapCodeToUse);
	}
	
	@Override
	public void setText(String stringMapAsString)
	{
		CodeList codes = createCodeListFromString(stringMapAsString);
		super.setText(codes.toString());
	}

	private CodeList createCodeListFromString(String StringMapAsString)
	{
		try
		{
			AbstractStringToStringMap stringMap = createStringKeyMap(StringMapAsString);
			String codeListAsString = stringMap.get(getMapCode());
			
			return new CodeList(codeListAsString);
		}
		catch(Exception e)
		{
			EAM.unexpectedErrorDialog(e);
			EAM.logException(e);
			return new CodeList();
		}
	}
	
	@Override
	public String getText()
	{
		try
		{
			AbstractStringToStringMap existingMap = createCurrentStringKeyMap();
			String key = getMapCode();
			String value = super.getText();
			put(existingMap, key, value);
			
			return existingMap.toString();
		}
		catch (Exception e)
		{
			EAM.unexpectedErrorDialog(e);
			EAM.logException(e);
			return "";
		}
	}

	
	abstract protected AbstractStringToStringMap createCurrentStringKeyMap() throws Exception;
	abstract protected AbstractStringToStringMap createStringKeyMap(String StringMapAsString) throws Exception;
	abstract protected void put(AbstractStringToStringMap existingMap, String key, String value);
}
