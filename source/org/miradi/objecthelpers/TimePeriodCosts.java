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
package org.miradi.objecthelpers;

import java.util.Set;
import java.util.Vector;

import org.miradi.main.EAM;
import org.miradi.objects.AccountingCode;
import org.miradi.objects.FundingSource;
import org.miradi.objects.ProjectResource;
import org.miradi.project.Project;
import org.miradi.utils.OptionalDouble;

public class TimePeriodCosts
{
	public TimePeriodCosts()
	{
		workUnitPacks = new Vector();
		expensesPacks = new Vector();

		totalExpenses = new OptionalDouble();
		totalWorkUnits = new OptionalDouble();
	}
	
	public TimePeriodCosts(TimePeriodCosts timePeriodCostsToUse)
	{
		this();
		add(timePeriodCostsToUse);
	}
	
	public TimePeriodCosts(ORef fundingSourceRef, ORef accountingCodeRef, OptionalDouble expenseToUse)
	{
		this();
		
		ensureFundingSource(fundingSourceRef);
		ensureAccountingCode(accountingCodeRef);
		
		addExpensesToTotal(expenseToUse);
		addToDataPacks(expensesPacks, new DataPack(ORef.INVALID, fundingSourceRef, accountingCodeRef, expenseToUse));
	}
	
	public TimePeriodCosts(ORef resourceRef, ORef fundingSourceRef,	ORef accountingCodeRef, OptionalDouble workUnits)
	{
		this();
		
		ensureResource(resourceRef);
		ensureFundingSource(fundingSourceRef);
		ensureAccountingCode(accountingCodeRef);
		
		addWorkUnitsToTotal(workUnits);
		addToDataPacks(workUnitPacks, new DataPack(resourceRef, fundingSourceRef, accountingCodeRef, workUnits));
	}

	public void add(TimePeriodCosts timePeriodCosts)
	{
		addExpensesToTotal(timePeriodCosts);
		addDataPack(expensesPacks, timePeriodCosts.expensesPacks);
		
		addWorkUnitsToTotal(timePeriodCosts);
		addDataPack(workUnitPacks, timePeriodCosts.workUnitPacks);
	}
	
	private void addDataPack(Vector<DataPack> packToUpdate, Vector<DataPack> packsToAdd)
	{
		if (packToUpdate == packsToAdd)
			throw new RuntimeException(EAM.text("Cannot add a vector to itself."));
		
		for(DataPack thisDataPack : packsToAdd)
		{
			addToDataPacks(packToUpdate, thisDataPack);
		}
	}
	
	private void addToDataPacks(Vector<DataPack> dataPacksToUpdate, DataPack dataPackToAdd)
	{
		dataPacksToUpdate.add(dataPackToAdd);
	}
	
	private void addWorkUnitsToTotal(TimePeriodCosts timePeriodCosts)
	{
		addWorkUnitsToTotal(timePeriodCosts.getTotalWorkUnits());
	}
	
	private void addWorkUnitsToTotal(OptionalDouble totalWorkUnitsToAdd)
	{
		totalWorkUnits = totalWorkUnits.add(totalWorkUnitsToAdd);	
	}
	
	public OptionalDouble getTotalWorkUnits()
	{
		return totalWorkUnits;
	}
	
	private void addExpensesToTotal(TimePeriodCosts timePeriodCostsToUse)
	{
		addExpensesToTotal(timePeriodCostsToUse.getTotalExpense());
	}

	private void addExpensesToTotal(OptionalDouble expense)
	{
		totalExpenses = totalExpenses.add(expense);
	}
	
	public OptionalDouble getTotalExpense()
	{
		return totalExpenses;
	}
	
	public OptionalDouble calculateTotalCost(Project projectToUse)
	{
		final OptionalDouble expenseToAdd = getTotalExpense();
		final OptionalDouble totalResourceCost = calculateResourcesTotalCost(projectToUse);
		
		return totalResourceCost.add(expenseToAdd);
	}
	
	private OptionalDouble calculateResourcesTotalCost(Project projectToUse)
	{
		OptionalDouble resourcesTotalCost = new OptionalDouble();
		Vector<DataPack> dataPacks = workUnitPacks;
		for(DataPack thisDataPack : dataPacks)
		{
			OptionalDouble costPerUnit = getCostPerUnit(projectToUse, thisDataPack.getResourceRef());
			OptionalDouble workUnits = thisDataPack.getQuantity();
			OptionalDouble multiplyValue = workUnits.multiply(costPerUnit);
			resourcesTotalCost = resourcesTotalCost.add(multiplyValue);
		}
		
		return resourcesTotalCost;
	}

	private OptionalDouble getCostPerUnit(Project projectToUse,	ORef projectResourceRef)
	{
		if (projectResourceRef.isInvalid())
			return new OptionalDouble(0.0);
		
		ProjectResource projectResource = ProjectResource.find(projectToUse, projectResourceRef);
		return new OptionalDouble(projectResource.getCostPerUnit());
	}
	
	public OptionalDouble getWorkUnitsForRef(ORef ref)
	{
		return getRolledUpQuantityForRef(workUnitPacks, ref);
	}
	
	public OptionalDouble getFundingSourceExpenses(ORef fundingSourceRef)
	{
		return getRolledUpQuantityForRef(expensesPacks, fundingSourceRef);
	}
	
	private OptionalDouble getRolledUpQuantityForRef(Vector<DataPack> dataPacksToSearch, ORef refToFindBy)
	{
		OptionalDouble totalQuantityForRef = new OptionalDouble();
		for(DataPack thisDataPack : dataPacksToSearch)
		{
			if (thisDataPack.containsRef(refToFindBy))
				totalQuantityForRef = totalQuantityForRef.add(thisDataPack.getQuantity());
		}
		
		return totalQuantityForRef;
	}
	
	protected void mergeAllTimePeriodCosts(TimePeriodCosts timePeriodCostsToMergeAdd)
	{
		mergeAllExpensePacksInPlace(timePeriodCostsToMergeAdd);
		mergeAllWorkUnitDataPackInPlace(timePeriodCostsToMergeAdd);
	}

	private void mergeAllExpensePacksInPlace(TimePeriodCosts timePeriodCostsToMergeAdd)
	{
		addExpensesToTotal(timePeriodCostsToMergeAdd);
		
		mergeDataPackSetInPlace(expensesPacks, timePeriodCostsToMergeAdd.expensesPacks);
	}
	
	public void mergeAllWorkUnitDataPackInPlace(TimePeriodCosts timePeriodCostsToMerge)
	{
		addWorkUnitsToTotal(timePeriodCostsToMerge);
		
		mergeDataPackSetInPlace(workUnitPacks, timePeriodCostsToMerge.workUnitPacks);
	}
	
	private void mergeDataPackSetInPlace(Vector<DataPack> dataPackToUpdate, Vector<DataPack> dataPackToMergeFrom)
	{
		for(DataPack thisDataPack : dataPackToMergeFrom)
		{
			addToDataPacks(dataPackToUpdate, thisDataPack);
		}
	}
	
	protected void mergeNonConflicting(TimePeriodCosts snapShotTimePeriodCosts, TimePeriodCosts timePeriodCostsToMerge) throws Exception
	{
		if (!snapShotTimePeriodCosts.hasExpenseData())
			mergeAllExpensePacksInPlace(timePeriodCostsToMerge);
		
		if (!snapShotTimePeriodCosts.hasTotalWorkUnitsData())
			mergeAllWorkUnitDataPackInPlace(timePeriodCostsToMerge);
	}
	
	public void filterWorkUnitRelated(ORefSet refsToRetain)
	{
		filterDataPacks(workUnitPacks, refsToRetain);
		updateTotalWorkUnits();
	}
	
	public void filterExpenseRelated(ORefSet refsToRetain)
	{
		filterDataPacks(expensesPacks, refsToRetain);
		updateTotalExpenses();
	}
	
	private void filterDataPacks(Vector<DataPack> dataPacks, ORefSet refsToRetain)
	{
		if (refsToRetain.size() == 0)
			return;
		
		if (refsToRetain.contains(ORef.INVALID))
			EAM.logError("WARNING: Filtering on invalid ref with no type");
		
		Vector<DataPack> dataPacksToRetain = new Vector();
		for(DataPack dataPackToFilter : dataPacks)
		{
			if (dataPackToFilter.containsAtleastOne(refsToRetain))
				dataPacksToRetain.add(dataPackToFilter);
		}
		
		dataPacks.retainAll(dataPacksToRetain);
	}
	
	private void updateTotalExpenses()
	{
		totalExpenses = getTotal(expensesPacks);		
	}

	private void updateTotalWorkUnits()
	{
		totalWorkUnits = getTotal(workUnitPacks);
	}
	
	private OptionalDouble getTotal(Vector<DataPack> dataPacks)
	{
		OptionalDouble totals = new OptionalDouble();
		for(DataPack dataPack: dataPacks)
		{
			totals = totals.add(dataPack.getQuantity());
		}
		
		return totals;
	}
	
	public void divideBy(OptionalDouble divideByValue)
	{
		divideByDataPacks(workUnitPacks, divideByValue);
		updateTotalWorkUnits();
		
		divideByDataPacks(expensesPacks, divideByValue);
		updateTotalExpenses();
	}
	
	private void divideByDataPacks(Vector<DataPack> dataPacksToDivide, OptionalDouble divideByValue)
	{
		for(DataPack dataPack : dataPacksToDivide)
		{
			dataPack.divideBy(divideByValue);
		}
	}
	
	@Override
	public boolean equals(Object rawOther)
	{
		if (! (rawOther instanceof TimePeriodCosts))
			return false;
		
		TimePeriodCosts other = (TimePeriodCosts) rawOther;
		if (!other.getTotalExpense().equals(getTotalExpense()))
			return false;
		
		if (!other.getTotalWorkUnits().equals(getTotalWorkUnits()))
			return false;
		
		if (!other.workUnitPacks.equals(workUnitPacks))
			return false;
		
		return other.expensesPacks.equals(expensesPacks);
	}
	
	@Override
	public int hashCode()
	{
		return totalExpenses.hashCode() + totalWorkUnits.hashCode();
	}
	
	@Override
	public String toString()
	{
		String asString = "";
		asString = "TotalExpenses = " + getTotalExpense() + "\n";		
		asString += "TotalWorkUnits = " + getTotalWorkUnits() + "\n";
		
		return asString;
	}

	public Set<ORef> getResourceRefSet()
	{
		return extractRefs(workUnitPacks, ProjectResource.getObjectType());
	}
	
	public Set<ORef> getFundingSourceWorkUnitsRefSet()
	{
		return extractRefs(workUnitPacks, FundingSource.getObjectType());
	}
	
	public Set<ORef> getFundingSourceExpensesRefSet()
	{
		return extractRefs(expensesPacks, FundingSource.getObjectType());
	}
	
	private ORefSet extractRefs(Vector<DataPack> dataPacksToUse, int type)
	{
		ORefSet extractedRefs = new ORefSet();
		for(DataPack dataPack : dataPacksToUse)
		{
			ORefSet containingRefs = dataPack.getContainingRefs();
			ORefSet filteredRefs = containingRefs.getFilteredBy(type);
			extractedRefs.addAll(filteredRefs);
		}
		
		return extractedRefs;
	}
	
	private boolean hasExpenseData()
	{
		return getTotalExpense().hasValue();
	}

	private boolean hasTotalWorkUnitsData()
	{
		return getTotalWorkUnits().hasValue();
	}
	
	private void ensureResource(ORef resourceRef)
	{
		if (resourceRef.isValid() && !ProjectResource.is(resourceRef))
			throw new RuntimeException(getWrongRefErrorMessage(resourceRef, "ProjectResource Ref"));
	}

	private void ensureFundingSource(ORef fundingSourceRef)
	{
		if (fundingSourceRef.isValid() && !FundingSource.is(fundingSourceRef))
			throw new RuntimeException(getWrongRefErrorMessage(fundingSourceRef, "FundingSource Ref"));
	}
	
	private void ensureAccountingCode(ORef accountingCodeRef)
	{
		if (accountingCodeRef.isValid() && !AccountingCode.is(accountingCodeRef))
			throw new RuntimeException(getWrongRefErrorMessage(accountingCodeRef, "AccountingCode Ref"));
	}
	
	private String getWrongRefErrorMessage(ORef ref, String substituionText)
	{
		return EAM.substitute(EAM.text("Was expecting a %s, instead got:\n" + ref.toString()), substituionText);
	}

	
	class DataPack 
	{
		public DataPack()
		{
			resourceRef = ORef.INVALID;
			fundingSourceRef = ORef.INVALID;
			accountingCodeRef = ORef.INVALID;
			quantity = new OptionalDouble();
		}
		
		public DataPack(ORef resourceRefToUse, ORef fundingSourceRefToUse, ORef accountingCodeRefToUse, OptionalDouble quantityToUse)
		{
			resourceRef = resourceRefToUse;
			fundingSourceRef = fundingSourceRefToUse;
			accountingCodeRef = accountingCodeRefToUse;
			quantity = quantityToUse;
		}
		
		public ORefSet getContainingRefs()
		{
			ORefSet allContainingRefs = new ORefSet();
			allContainingRefs.add(getResourceRef());
			allContainingRefs.add(getFundingSourceRef());
			allContainingRefs.add(getAccountingCodeRef());
			
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
			
			return fundingSourceRef.equals(refToMatch);
		}
		
		public void addQuantity(OptionalDouble quantityToAdd)
		{
			quantity = quantity.add(quantityToAdd);
		}
		
		public void divideBy(OptionalDouble divideBy)
		{
			quantity = quantity.divideBy(divideBy);
		}
		
		private ORef getResourceRef()
		{
			return resourceRef;
		}
		
		private ORef getFundingSourceRef()
		{
			return fundingSourceRef;
		}
		
		private ORef getAccountingCodeRef()
		{
			return accountingCodeRef;
		}
		
		private OptionalDouble getQuantity()
		{
			return quantity;
		}
		
		@Override
		public int hashCode()
		{
			return fundingSourceRef.hashCode() + resourceRef.hashCode() + accountingCodeRef.hashCode();
		}
		
		@Override
		public boolean equals(Object rawOther)
		{
			if (!(rawOther instanceof DataPack))
				return false;
			
			DataPack other = (DataPack) rawOther;
			if (!fundingSourceRef.equals(other.fundingSourceRef))
				return false;
			
			if (!resourceRef.equals(other.resourceRef))
				return false;
			
			if (!accountingCodeRef.equals(other.accountingCodeRef))
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
		private OptionalDouble quantity;
	}
	
	private OptionalDouble totalExpenses;
	private OptionalDouble totalWorkUnits;
	
	private Vector<DataPack> workUnitPacks;
	private Vector<DataPack> expensesPacks;
}
