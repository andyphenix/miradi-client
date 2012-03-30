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

import org.miradi.ids.AccountingCodeId;
import org.miradi.ids.BaseId;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.project.ObjectManager;
import org.miradi.project.Project;
import org.miradi.schemas.AccountingCodeSchema;
import org.miradi.utils.EnhancedJsonObject;

public class AccountingCode extends AbstractBudgetCategoryObject
{
	public AccountingCode(ObjectManager objectManager, BaseId idToUse)
	{
		super(objectManager, idToUse, new AccountingCodeSchema());
	}
	
	public AccountingCode(ObjectManager objectManager, int idAsInt, EnhancedJsonObject json) throws Exception
	{
		super(objectManager, new AccountingCodeId(idAsInt), json, new AccountingCodeSchema());
	}
	
	@Override
	public int getType()
	{
		return AccountingCodeSchema.getObjectType();
	}
	
	@Override
	public String getTypeName()
	{
		return AccountingCodeSchema.OBJECT_NAME;
	}

	@Override
	public int[] getTypesThatCanOwnUs()
	{
		return NO_OWNERS;
	}
	
	@Override
	public String toString()
	{
		return toString(EAM.text("Label|(Undefined Accounting Code)"));
	}
	
	public static boolean is(BaseObject baseObject)
	{
		return is(baseObject.getType());
	}
	
	public static boolean is(int objectType)
	{
		return objectType == AccountingCodeSchema.getObjectType();
	}
		
	public static boolean is(ORef ref)
	{
		return is(ref.getObjectType());
	}
	
	public static AccountingCode find(ObjectManager objectManager, ORef accountingCodeRef)
	{
		return (AccountingCode) objectManager.findObject(accountingCodeRef);
	}
	
	public static AccountingCode find(Project project, ORef accountingCodeRef)
	{
		return find(project.getObjectManager(), accountingCodeRef);
	}

}