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

import org.miradi.objecthelpers.AbstractStringToStringMap;
import org.miradi.objecthelpers.CodeToCodeListMap;
import org.miradi.objecthelpers.ORef;
import org.miradi.project.Project;
import org.miradi.questions.ChoiceQuestion;
import org.miradi.utils.CodeList;
   
public class CodeCodeListMapEditorField extends AbstractCodeCodeListMapEditorField
{
	public CodeCodeListMapEditorField(Project projectToUse, ORef refToUse, String tagToUse, ChoiceQuestion questionToUse, String mapKeyCodeToUse)
	{
		super(projectToUse, refToUse, tagToUse, questionToUse, mapKeyCodeToUse);
	}

	@Override
	protected AbstractStringToStringMap createCurrentStringKeyMap() throws Exception
	{
		return new CodeToCodeListMap(getProject().getObjectData(getORef(), getTag()));
	}

	@Override
	protected AbstractStringToStringMap createStringKeyMap(String StringMapAsString) throws Exception
	{
		return new CodeToCodeListMap(StringMapAsString);
	}
	
	@Override
	protected void put(AbstractStringToStringMap existingMap, String key, String value) throws ParseException
	{
		CodeToCodeListMap map = (CodeToCodeListMap) existingMap;
		CodeList codeList = new CodeList(value);
		map.putCodeList(key, codeList);
	}
}
