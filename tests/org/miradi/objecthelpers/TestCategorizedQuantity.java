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

package org.miradi.objecthelpers;

import org.miradi.ids.BaseId;
import org.miradi.main.TestCaseWithProject;
import org.miradi.schemas.AccountingCodeSchema;
import org.miradi.schemas.BudgetCategoryOneSchema;
import org.miradi.schemas.BudgetCategoryTwoSchema;
import org.miradi.schemas.FundingSourceSchema;
import org.miradi.schemas.ProjectResourceSchema;
import org.miradi.utils.OptionalDouble;

public class TestCategorizedQuantity extends TestCaseWithProject
{
	public TestCategorizedQuantity(String name)
	{
		super(name);
	}

	public void testBasics() throws Exception
	{
		ORef resourceRef = getProject().createProjectResource().getRef();
		ORef fundingSourceRef = getProject().createFundingSource().getRef();
		ORef accountingCodeRef = getProject().createAccountingCode().getRef();
		ORef categoryOneRef = getProject().createCategoryOne().getRef();
		ORef categoryTwoRef = getProject().createCategoryTwo().getRef();
		CategorizedQuantity categorizedQuantity = new CategorizedQuantity(resourceRef, fundingSourceRef, accountingCodeRef, categoryOneRef, categoryTwoRef, new OptionalDouble(14.0));
		
		verifyContainsMethods(categorizedQuantity, resourceRef);
		verifyContainsMethods(categorizedQuantity, fundingSourceRef);
		verifyContainsMethods(categorizedQuantity, accountingCodeRef);
		verifyContainsMethods(categorizedQuantity, fundingSourceRef);
		verifyContainsMethods(categorizedQuantity, categoryOneRef);
		verifyContainsMethods(categorizedQuantity, categoryTwoRef);
	}
	
	public void testUntypedInvalidsPassedToConstructor()
	{
		ORef resourceRef = ORef.INVALID;
		ORef fundingSourceRef = ORef.INVALID;
		ORef accountingCodeRef = ORef.INVALID;
		ORef categoryOneRef = ORef.INVALID;
		ORef categoryTwoRef = ORef.INVALID;
		CategorizedQuantity categorizedQuantity = new CategorizedQuantity(resourceRef, fundingSourceRef, accountingCodeRef, categoryOneRef, categoryTwoRef, new OptionalDouble(14.0));
		
		verifyContainsMethods(categorizedQuantity, new ORef(ProjectResourceSchema.getObjectType(), BaseId.INVALID));
		verifyContainsMethods(categorizedQuantity, new ORef(FundingSourceSchema.getObjectType(), BaseId.INVALID));
		verifyContainsMethods(categorizedQuantity, new ORef(AccountingCodeSchema.getObjectType(), BaseId.INVALID));
		verifyContainsMethods(categorizedQuantity, new ORef(BudgetCategoryOneSchema.getObjectType(), BaseId.INVALID));
		verifyContainsMethods(categorizedQuantity, new ORef(BudgetCategoryTwoSchema.getObjectType(), BaseId.INVALID));
	}

	private void verifyContainsMethods(CategorizedQuantity categorizedQuantity, ORef ref)
	{
		assertTrue("sould contain atleast one of ref?", categorizedQuantity.containsAtleastOne(new ORefSet(ref)));
		assertTrue("sould contain ref?", categorizedQuantity.containsRef(ref));
		assertTrue("should contain ref?", categorizedQuantity.getContainingRefs().contains(ref));
	}
	
	public void testDivideBy()
	{
		OptionalDouble quantity = new OptionalDouble(14.0);
		CategorizedQuantity categorizedQuantity = new CategorizedQuantity(ORef.INVALID, ORef.INVALID, ORef.INVALID, ORef.INVALID, ORef.INVALID, quantity);
		assertEquals("incorrect quantity?", quantity, categorizedQuantity.getQuantity());
		
		categorizedQuantity.divideBy(new OptionalDouble(2.0));
		assertEquals("incorrect divide by result?", new OptionalDouble(7.0), categorizedQuantity.getQuantity());
	}
	
	public void testEquals() throws Exception
	{
		CategorizedQuantity categorizedQuantity1 = createSampleCategorizedQuantity();
		CategorizedQuantity categorizedQuantity2 = createSampleCategorizedQuantity();
		assertTrue("should be equal to itself?", categorizedQuantity1.equals(categorizedQuantity1));
		assertFalse("categorized Quantity objects with different containing refs should not be the same", categorizedQuantity1.equals(categorizedQuantity2));
	}
	
	public void testContainsAll() throws Exception
	{
		ORef resourceRef = getProject().createProjectResource().getRef();
		ORef fundingSourceRef = getProject().createFundingSource().getRef();
		ORef accountingCodeRef = getProject().createAccountingCode().getRef();
		ORef categoryOneRef = getProject().createCategoryOne().getRef();
		ORef categoryTwoRef = getProject().createCategoryTwo().getRef();
		
		CategorizedQuantity categorizedQuantity = new CategorizedQuantity(resourceRef, fundingSourceRef, accountingCodeRef, categoryOneRef, categoryTwoRef, new OptionalDouble(14.0));
		
		ORefSet refs = new ORefSet();
		addAndVerifyTrueContainsAll(categorizedQuantity, refs, resourceRef);
		addAndVerifyTrueContainsAll(categorizedQuantity, refs, fundingSourceRef);
		addAndVerifyTrueContainsAll(categorizedQuantity, refs, accountingCodeRef);
		addAndVerifyTrueContainsAll(categorizedQuantity, refs, categoryOneRef);
		addAndVerifyTrueContainsAll(categorizedQuantity, refs, categoryTwoRef);
		
		ORef resourceRefNotContained = getProject().createProjectResource().getRef();
		refs.add(resourceRefNotContained);
		assertFalse("should not contain all?", categorizedQuantity.containsAll(refs));
		assertTrue("should contain empty list?", categorizedQuantity.containsAll(new ORefSet()));
		
		ORefSet listWithInvalidRef = new ORefSet(ORef.INVALID);
		assertFalse("should not contain list with invalid", categorizedQuantity.containsAll(listWithInvalidRef));

		ORefSet listWithSomeMatchingRefs = new ORefSet();
		listWithSomeMatchingRefs.add(resourceRef);
		listWithSomeMatchingRefs.add(new ORef(FundingSourceSchema.getObjectType(), new BaseId(9999)));
		assertFalse("should not contain list with non matching refs", categorizedQuantity.containsAll(listWithSomeMatchingRefs));
		
		ORefSet listWithTwoMatchingRefs = new ORefSet();
		listWithTwoMatchingRefs.add(resourceRef);
		listWithTwoMatchingRefs.add(fundingSourceRef);
		assertTrue("should contain all refs?", categorizedQuantity.containsAll(listWithTwoMatchingRefs));
	}

	private void addAndVerifyTrueContainsAll(CategorizedQuantity categorizedQuantity, ORefSet refs, ORef resourceRef)
	{
		refs.add(resourceRef);
		assertTrue("should contain all?", categorizedQuantity.containsAll(refs));
	}

	private CategorizedQuantity createSampleCategorizedQuantity() throws Exception
	{
		ORef resourceRef = getProject().createProjectResource().getRef();
		ORef fundingSourceRef = getProject().createFundingSource().getRef();
		ORef accountingCodeRef = getProject().createAccountingCode().getRef();
		ORef categoryOneRef = getProject().createCategoryOne().getRef();
		ORef categoryTwoRef = getProject().createCategoryTwo().getRef();
		
		return new CategorizedQuantity(resourceRef, fundingSourceRef, accountingCodeRef, categoryOneRef, categoryTwoRef, new OptionalDouble(14.0));
	}
}
