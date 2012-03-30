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

import org.miradi.ids.BaseId;
import org.miradi.ids.FundingSourceId;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.project.ObjectManager;
import org.miradi.project.Project;
import org.miradi.schemas.FundingSourceSchema;
import org.miradi.utils.EnhancedJsonObject;

public class FundingSource extends AbstractBudgetCategoryObject
{
	public FundingSource(ObjectManager objectManager, BaseId idToUse)
	{
		super(objectManager, idToUse, new FundingSourceSchema());
	}
	
	public FundingSource(ObjectManager objectManager, int idAsInt, EnhancedJsonObject json) throws Exception
	{
		super(objectManager, new FundingSourceId(idAsInt), json, new FundingSourceSchema());
	}
	
	@Override
	public int getType()
	{
		return FundingSourceSchema.getObjectType();
	}

	@Override
	public String getTypeName()
	{
		return FundingSourceSchema.OBJECT_NAME;
	}

	@Override
	public int[] getTypesThatCanOwnUs()
	{
		return NO_OWNERS;
	}
	
	@Override
	public String toString()
	{
		return toString(EAM.text("Label|(Undefined Funding Source)"));
	}

	public static FundingSource find(ObjectManager objectManager, ORef fundingSourceRef)
	{
		return (FundingSource) objectManager.findObject(fundingSourceRef);
	}
	
	public static FundingSource find(Project project, ORef fundingSourceRef)
	{
		return find(project.getObjectManager(), fundingSourceRef);
	}
	
	public static boolean is(BaseObject baseObject)
	{
		return is(baseObject.getType());
	}
	
	public static boolean is(int objectType)
	{
		return objectType == FundingSourceSchema.getObjectType();
	}
		
	public static boolean is(ORef ref)
	{
		return is(ref.getObjectType());
	}
}