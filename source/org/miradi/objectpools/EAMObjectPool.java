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
package org.miradi.objectpools;

import java.util.Vector;

import org.miradi.ids.BaseId;
import org.miradi.ids.IdList;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.BaseObject;

public class EAMObjectPool extends ObjectPool
{
	public EAMObjectPool(int objectTypeToStore)
	{
		super(objectTypeToStore);
	}
	
	public BaseObject findObject(BaseId id)
	{
		return (BaseObject)getRawObject(id);
	}
	
	public BaseObject findObject(ORef ref)
	{
		if(ref.getObjectType() != getObjectType())
			return null;
		
		return findObject(ref.getObjectId());
	}
	
	public ORefList getORefList()
	{
		return new ORefList(getObjectType(), new IdList(getObjectType(), getIds()));
	}

	public ORefList getSortedRefList()
	{
		ORefList sortedList = new ORefList(getObjectType(), getIdList());
		sortedList.sort();
		return sortedList;
	}
	
	public BaseObject[] getAllObjectsAsArray()
	{
		return getAllObjects().toArray(new BaseObject[0]);
	}
	
	public Vector<BaseObject> getAllObjects()
	{
		Vector<BaseObject> allObjects = new Vector<BaseObject>();
		ORefList allRefs = getORefList();
		for (int index = 0; index < allRefs.size(); ++index)
		{
			ORef ref = allRefs.get(index);
			allObjects.add(findObject(ref));
		}
		
		return allObjects;
	}	
}
