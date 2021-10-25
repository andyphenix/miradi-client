/* 
Copyright 2005-2021, Foundations of Success, Bethesda, Maryland
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

package org.miradi.migrations;

import org.miradi.migrations.forward.MigrationTo24;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objectpools.AccountingClassificationAssociationPool;
import org.miradi.objects.ExpenseAssignment;
import org.miradi.objects.ResourceAssignment;

public class TestMigrationTo24 extends AbstractTestMigration
{
	public TestMigrationTo24(String name)
	{
		super(name);
	}
	
	public void testResourceAssignmentAccountingClassificationsRemovedAfterReverseMigration() throws Exception
	{
		ResourceAssignment resourceAssignment = getProject().createAndPopulateResourceAssignment();
		String accountingClassifications = resourceAssignment.getData(MigrationTo24.TAG_ACCOUNTING_CLASSIFICATION_CONTAINER);
		assertNotNull(accountingClassifications);

		AccountingClassificationAssociationPool accountingClassificationPool = getProject().getAccountingClassificationAssociationPool();
		assertNotNull(accountingClassificationPool);

		RawProject rawProject = reverseMigrate(new VersionRange(MigrationTo24.VERSION_TO));
        RawObject rawResourceAssignment = rawProject.findObject(resourceAssignment.getRef());
        assertNotNull(rawResourceAssignment);
		assertFalse("Field should have been removed during reverse migration?", rawResourceAssignment.containsKey(MigrationTo24.TAG_ACCOUNTING_CLASSIFICATION_CONTAINER));

		RawPool rawAccountingClassificationPool = rawProject.getRawPoolForType(ObjectType.ACCOUNTING_CLASSIFICATION_ASSOCIATION);
		assertNull(rawAccountingClassificationPool);
	}
	
	public void testExpenseAssignmentAccountingClassificationsRemovedAfterReverseMigration() throws Exception
	{
		ExpenseAssignment expenseAssignment = getProject().createAndPopulateExpenseAssignment();
		String accountingClassifications = expenseAssignment.getData(MigrationTo24.TAG_ACCOUNTING_CLASSIFICATION_CONTAINER);
		assertNotNull(accountingClassifications);

		AccountingClassificationAssociationPool accountingClassificationPool = getProject().getAccountingClassificationAssociationPool();
		assertNotNull(accountingClassificationPool);

		RawProject rawProject = reverseMigrate(new VersionRange(MigrationTo24.VERSION_TO));
        RawObject rawExpenseAssignment = rawProject.findObject(expenseAssignment.getRef());
        assertNotNull(rawExpenseAssignment);
		assertFalse("Field should have been removed during reverse migration?", rawExpenseAssignment.containsKey(MigrationTo24.TAG_ACCOUNTING_CLASSIFICATION_CONTAINER));

		RawPool rawAccountingClassificationPool = rawProject.getRawPoolForType(ObjectType.ACCOUNTING_CLASSIFICATION_ASSOCIATION);
		assertNull(rawAccountingClassificationPool);
	}
	
	@Override
	protected int getFromVersion()
	{
		return MigrationTo24.VERSION_FROM;
	}
	
	@Override
	protected int getToVersion()
	{
		return MigrationTo24.VERSION_TO;
	}
}
