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

package org.miradi.dialogs.planning.treenodes;

import org.miradi.ids.BaseId;
import org.miradi.main.EAM;
import org.miradi.objects.BaseObject;
import org.miradi.project.ObjectManager;
import org.miradi.schemas.UnspecifiedBaseObjectSchema;

public class UnspecifiedBaseObject extends BaseObject
{
	public UnspecifiedBaseObject(ObjectManager objectManagerToUse, int objectTypeToUse, String objectNameToUse)
	{
		super(objectManagerToUse, BaseId.INVALID, new UnspecifiedBaseObjectSchema(objectTypeToUse, objectNameToUse));
	}

	@Override
	public String getLabel()
	{
		return toString();
	}
	
	@Override
	public String toString()
	{
		return EAM.text("Not Specified");
	}
	
	@Override
	public int[] getTypesThatCanOwnUs()
	{
		return NO_OWNERS;
	}
}