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

package org.miradi.objects;

import org.miradi.ids.BaseId;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.project.ObjectManager;
import org.miradi.project.Project;
import org.miradi.schemas.BudgetCategoryOneSchema;

public class BudgetCategoryOne extends AbstractBudgetCategoryObject
{
	public BudgetCategoryOne(ObjectManager objectManager, BaseId idToUse)
	{
		super(objectManager, idToUse, createSchema());
	}

	public static BudgetCategoryOneSchema createSchema()
	{
		return new BudgetCategoryOneSchema();
	}
		
	@Override
	public int[] getTypesThatCanOwnUs()
	{
		return NO_OWNERS;
	}
	
	@Override
	public String toString()
	{
		return toString(EAM.text("Label|Budget Category One"));
	}
	
	public static boolean is(BaseObject object)
	{
		return is(object.getRef());
	}

	public static boolean is(ORef ref)
	{
		return is(ref.getObjectType());
	}
	
	public static boolean is(int objectType)
	{
		return objectType == BudgetCategoryOneSchema.getObjectType();
	}
	
	public static BudgetCategoryOne find(ObjectManager objectManager, ORef ref)
	{
		return (BudgetCategoryOne) objectManager.findObject(ref);
	}
	
	public static BudgetCategoryOne find(Project project, ORef ref)
	{
		return find(project.getObjectManager(), ref);
	}
}
