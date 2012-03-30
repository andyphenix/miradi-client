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
import org.miradi.objecthelpers.DateUnit;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objecthelpers.TimePeriodCosts;
import org.miradi.objecthelpers.TimePeriodCostsMap;
import org.miradi.project.ObjectManager;
import org.miradi.project.Project;
import org.miradi.schemas.ExpenseAssignmentSchema;
import org.miradi.schemas.FundingSourceSchema;
import org.miradi.utils.EnhancedJsonObject;
import org.miradi.utils.OptionalDouble;

public class ExpenseAssignment extends Assignment
{
	public ExpenseAssignment(ObjectManager objectManager, BaseId idToUse)
	{
		super(objectManager, idToUse, new ExpenseAssignmentSchema());
	}
		
	public ExpenseAssignment(ObjectManager objectManager, int idAsInt, EnhancedJsonObject json) throws Exception
	{
		super(objectManager, new BaseId(idAsInt), json, new ExpenseAssignmentSchema());
	}
		
	@Override
	protected TimePeriodCosts createTimePeriodCosts(OptionalDouble quantity)
	{
		return new TimePeriodCosts(getFundingSourceRef(), getAccountingCodeRef(), getCategoryOneRef(), getCategoryTwoRef(), quantity);
	}
	
	@Override
	protected String getFundingSourceTag()
	{
		return TAG_FUNDING_SOURCE_REF;
	}

	@Override
	protected String getAccountingCodeTag()
	{
		return TAG_ACCOUNTING_CODE_REF;
	}
	
	@Override
	public ORef getFundingSourceRef()
	{
		ORef ref = getRefData(TAG_FUNDING_SOURCE_REF);
		if (ref.isInvalid())
			return ORef.createInvalidWithType(FundingSourceSchema.getObjectType());
		
		return ref;
	}
	
	@Override
	public ORef getAccountingCodeRef()
	{
		ORef ref = getRefData(TAG_ACCOUNTING_CODE_REF);
		if (ref.isInvalid())
			return ORef.createInvalidWithType(AccountingCode.getObjectType());
		
		return ref;
	}
	
	@Override
	public TimePeriodCostsMap getTotalTimePeriodCostsMap() throws Exception
	{
		return getTimePeriodCostsMap(TAG_EXPENSE_ASSIGNMENT_REFS);
	}
	
	@Override
	public boolean isAssignmentDataSuperseded(DateUnit dateUnit) throws Exception
	{
		return getOwner().hasAnySubtaskExpenseData(dateUnit);
	}
	
	@Override
	public String toString()
	{
		return getLabel();
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
	
	public static int getObjectType()
	{
		return ObjectType.EXPENSE_ASSIGNMENT;
	}
	
	public static boolean is(ORef ref)
	{
		return is(ref.getObjectType());
	}
	
	public static boolean is(int objectType)
	{
		return objectType == getObjectType();
	}
	
	public static boolean is(BaseObject baseObject)
	{
		return is(baseObject.getType());
	}
	
	public static ExpenseAssignment find(ObjectManager objectManager, ORef expenseRef)
	{
		return (ExpenseAssignment) objectManager.findObject(expenseRef);
	}
	
	public static ExpenseAssignment find(Project project, ORef expenseRef)
	{
		return find(project.getObjectManager(), expenseRef);
	}
	
	public static final String OBJECT_NAME = "ExpenseAssignment";
	
	public static final String TAG_ACCOUNTING_CODE_REF = "AccountingCodeRef";
	public static final String TAG_FUNDING_SOURCE_REF = "FundingSourceRef";
}
