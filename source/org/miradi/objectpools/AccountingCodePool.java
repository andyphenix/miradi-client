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
package org.miradi.objectpools;

import org.miradi.ids.BaseId;
import org.miradi.ids.IdAssigner;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.AccountingCode;
import org.miradi.objects.BaseObject;
import org.miradi.project.ObjectManager;

public class AccountingCodePool extends AbstractBudgetCategoryObjectPool
{
	public AccountingCodePool(IdAssigner idAssignerToUse)
	{
		super(idAssignerToUse, ObjectType.ACCOUNTING_CODE);
	}
	
	public AccountingCode find(BaseId id)
	{
		return (AccountingCode)findObject(id);
	}

	@Override
	BaseObject createRawObject(ObjectManager objectManager, BaseId actualId)
	{
		return new AccountingCode(objectManager, actualId);
	}
	
	public AccountingCode[] getAllAccountingCodes()
	{
		BaseId[] allIds = getIds();
		AccountingCode[] allAccountingCodes = new AccountingCode[allIds.length];
		for (int i = 0; i < allAccountingCodes.length; i++)
			allAccountingCodes[i] = find(allIds[i]);
			
		return allAccountingCodes;
	}

}