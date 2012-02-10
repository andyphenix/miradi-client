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
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.CodeToUserStringMap;

public class ObjectStringMapInputField extends ObjectStringInputField
{
	public ObjectStringMapInputField(MainWindow mainWindowToUse, ORef refToUse, String tagToUse, String codeToUse, int columnsToUse)
	{
		this (mainWindowToUse, refToUse.getObjectType(), refToUse.getObjectId(), tagToUse, codeToUse, columnsToUse);
	}
	
	public ObjectStringMapInputField(MainWindow mainWindowToUse, int objectTypeToUse, BaseId objectIdToUse, String tagToUse, String codeToUse, int columnsToUse)
	{
		super(mainWindowToUse, objectTypeToUse, objectIdToUse, tagToUse, columnsToUse);
		
		code = codeToUse;
	}
	
	@Override
	public String getText()
	{
		if (getORef().isInvalid())
			return "";
						
		try
		{
			String mapAsString = getProject().getObjectData(getORef(), getTag());
			CodeToUserStringMap stringMap = new CodeToUserStringMap(mapAsString);
			stringMap.putUserString(code, super.getText());
			
			return stringMap.toJsonString();
		}
		catch(ParseException e)
		{
			EAM.logException(e);
			EAM.unexpectedErrorDialog(e);
			return "";
		}
	}
	
	@Override
	public void setText(String newValue)
	{
		try
		{
			CodeToUserStringMap stringMap = new CodeToUserStringMap(newValue);
			String value = stringMap.get(code);
			super.setText(value);
		}
		catch (Exception e)
		{
			EAM.unexpectedErrorDialog(e);
			EAM.logException(e);
		}
	}
	
	private String code;
}
