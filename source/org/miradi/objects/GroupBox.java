/* 
Copyright 2005-2012, Foundations of Success, Bethesda, Maryland 
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
package org.miradi.objects;

import org.miradi.ids.FactorId;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.project.ObjectManager;
import org.miradi.project.Project;
import org.miradi.schemas.GroupBoxSchema;
import org.miradi.utils.EnhancedJsonObject;

public class GroupBox extends Factor
{
	public GroupBox(ObjectManager objectManager, FactorId idToUse)
	{
		super(objectManager, idToUse, new GroupBoxSchema());
		clear();
	}
		
	public GroupBox(ObjectManager objectManager, FactorId idToUse, EnhancedJsonObject json) throws Exception
	{
		super(objectManager, idToUse, json, new GroupBoxSchema());
	}
	
	@Override
	public int getType()
	{
		return getObjectType();
	}
	
	@Override
	public String getTypeName()
	{
		return OBJECT_NAME;
	}

	@Override
	public int[] getTypesThatCanOwnUs()
	{
		return NO_OWNERS;
	}
	
	public static int getObjectType()
	{
		return ObjectType.GROUP_BOX;
	}
	
	@Override
	public boolean isGroupBox()
	{
		return true;
	}
	
	@Override
	public boolean canHaveIndicators()
	{
		return false;
	}

	@Override
	public ORefList getOwnedObjects(int objectType)
	{
		return new ORefList();
	}
	
	public static boolean is(ORef ref)
	{
		return is(ref.getObjectType());
	}
	
	public static boolean is(int objectType)
	{
		return objectType == getObjectType();
	}
	
	public static GroupBox find(ObjectManager objectManager, ORef groupBoxRef)
	{
		return (GroupBox) objectManager.findObject(groupBoxRef);
	}
	
	public static GroupBox find(Project project, ORef groupBoxRef)
	{
		return find(project.getObjectManager(), groupBoxRef);
	}
	
	public static final String OBJECT_NAME = "GroupBox";
}
