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
import org.miradi.schemas.ThreatReductionResultSchema;
import org.miradi.utils.EnhancedJsonObject;

public class ThreatReductionResult extends Factor
{
	public ThreatReductionResult(ObjectManager objectManager, FactorId idToUse)
	{
		super(objectManager, idToUse, new ThreatReductionResultSchema());
	}
	
	public ThreatReductionResult(ObjectManager objectManager, FactorId idToUse, EnhancedJsonObject json) throws Exception
	{
		super(objectManager, idToUse, json, new ThreatReductionResultSchema());
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
		return ObjectType.THREAT_REDUCTION_RESULT;
	}
	
	@Override
	public boolean isThreatReductionResult()
	{
		return true;
	}
	
	@Override
	public boolean canHaveObjectives()
	{
		return true;
	}
	
	@Override
	public ORefList getOwnedObjects(int objectType)
	{
		ORefList list = super.getOwnedObjects(objectType);
		
		switch(objectType)
		{
			case ObjectType.OBJECTIVE: 
				list.addAll(new ORefList(objectType, getObjectiveIds()));
				break;
		}
		return list;
	}
	
	public String getRelatedDirectThreatRefAsString()
	{
		return getData(TAG_RELATED_DIRECT_THREAT_REF);
	}
	
	public ORef getRelatedThreatRef()
	{
		if (getRelatedDirectThreatRefAsString().length() == 0)
			return ORef.INVALID;
		
		return ORef.createFromString(getRelatedDirectThreatRefAsString());
	}
	
	public static boolean is(BaseObject baseObject)
	{
		return is(baseObject.getType());
	}
	
	public static boolean is(ORef ref)
	{
		return is(ref.getObjectType());
	}
	
	public static boolean is(int objectType)
	{
		return objectType == getObjectType();
	}
	
	public static ThreatReductionResult find(ObjectManager objectManager, ORef threatReductionResultRef)
	{
		return (ThreatReductionResult) objectManager.findObject(threatReductionResultRef);
	}
	
	public static ThreatReductionResult find(Project project, ORef threatReductionResultRef)
	{
		return find(project.getObjectManager(), threatReductionResultRef);
	}
	
	public static final String TAG_RELATED_DIRECT_THREAT_REF = "RelatedDirectThreatRef";
	public static final String OBJECT_NAME = "ThreatReductionResult";
}
