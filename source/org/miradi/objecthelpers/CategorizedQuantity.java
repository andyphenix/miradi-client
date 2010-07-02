/* 
Copyright 2005-2010, Foundations of Success, Bethesda, Maryland 
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

package org.miradi.objecthelpers;

import org.miradi.objects.AccountingCode;
import org.miradi.objects.CategoryOne;
import org.miradi.objects.CategoryTwo;
import org.miradi.objects.FundingSource;
import org.miradi.objects.ProjectResource;
import org.miradi.utils.OptionalDouble;

public class CategorizedQuantity 
{
	public CategorizedQuantity(ORef resourceRefToUse, ORef fundingSourceRefToUse, ORef accountingCodeRefToUse, ORef categoryOneRefToUse, ORef categoryTwoRefToUse, OptionalDouble quantityToUse)
	{
		resourceRefToUse.ensureValidType(ProjectResource.getObjectType());
		fundingSourceRefToUse.ensureValidType(FundingSource.getObjectType());
		accountingCodeRefToUse.ensureValidType(AccountingCode.getObjectType());
		categoryOneRefToUse.ensureValidType(CategoryOne.getObjectType());
		categoryTwoRefToUse.ensureValidType(CategoryTwo.getObjectType());
		
		resourceRef = resourceRefToUse;
		fundingSourceRef = fundingSourceRefToUse;
		accountingCodeRef = accountingCodeRefToUse;
		categoryOneRef = categoryOneRefToUse;
		categoryTwoRef = categoryTwoRefToUse;
		quantity = quantityToUse;
	}
	
	public ORefSet getContainingRefs()
	{
		ORefSet allContainingRefs = new ORefSet();
		allContainingRefs.add(getResourceRef());
		allContainingRefs.add(fundingSourceRef);
		allContainingRefs.add(accountingCodeRef);
		allContainingRefs.add(categoryOneRef);
		allContainingRefs.add(categoryTwoRef);
		
		return allContainingRefs;
	}
	
	public boolean containsAtleastOne(ORefSet refsToRetain)
	{
		ORefSet containingRefs = getContainingRefs();
		containingRefs.retainAll(refsToRetain);
		
		return containingRefs.size() > 0;
	}
	
	public boolean containsRef(ORef refToMatch)
	{
		if (resourceRef.equals(refToMatch))
			return true;
		
		if (accountingCodeRef.equals(refToMatch))
			return true;
		
		if (fundingSourceRef.equals(refToMatch))
			return true;
		
		if (categoryOneRef.equals(refToMatch))
			return true;
		
		return categoryTwoRef.equals(refToMatch);
	}
	
	public void divideBy(OptionalDouble divideBy)
	{
		quantity = quantity.divideBy(divideBy);
	}
	
	public ORef getResourceRef()
	{
		return resourceRef;
	}
	
	public OptionalDouble getQuantity()
	{
		return quantity;
	}
	
	@Override
	public int hashCode()
	{
		return fundingSourceRef.hashCode() + resourceRef.hashCode() + accountingCodeRef.hashCode() + categoryOneRef.hashCode() + categoryTwoRef.hashCode();
	}
	
	@Override
	public boolean equals(Object rawOther)
	{
		if (!(rawOther instanceof CategorizedQuantity))
			return false;
		
		CategorizedQuantity other = (CategorizedQuantity) rawOther;
		if (!fundingSourceRef.equals(other.fundingSourceRef))
			return false;
		
		if (!resourceRef.equals(other.resourceRef))
			return false;
		
		if (!accountingCodeRef.equals(other.accountingCodeRef))
			return false;
		
		if (!categoryOneRef.equals(other.categoryOneRef))
			return false;
		
		if (!categoryTwoRef.equals(other.categoryTwoRef))
			return false;
		
		return quantity.equals(other.quantity);
	}
	
	@Override
	public String toString()
	{
		return "rsourceRef=" + resourceRef + " fundingSourceRef=" + fundingSourceRef + " accountingCodeRef=" + accountingCodeRef + " quantiy=" + quantity; 
	}
	
	private ORef resourceRef;
	private ORef fundingSourceRef;
	private ORef accountingCodeRef;
	private ORef categoryOneRef;
	private ORef categoryTwoRef;
	private OptionalDouble quantity;
}