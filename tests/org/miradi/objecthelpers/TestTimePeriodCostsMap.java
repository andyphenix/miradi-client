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

import org.miradi.main.TestCaseWithProject;
import org.miradi.objects.ProjectResource;
import org.miradi.utils.DateRange;
import org.miradi.utils.OptionalDouble;

public class TestTimePeriodCostsMap extends TestCaseWithProject
{
	public TestTimePeriodCostsMap(String name)
	{
		super(name);
	}
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		
		dateUnit2008 = createSingleYearDateUnit(2008);	
	}
	
	public void testBasics() throws Exception
	{
		getProject().setProjectDates(2005, 2011);
		TimePeriodCostsMap timePeriodCostsMap = new TimePeriodCostsMap();
		assertTrue("time period costs map is not empty?", timePeriodCostsMap.isEmpty());
		
		ProjectResource projectResource = createProjectResource();
		TimePeriodCosts timePeriodCosts1 = getProject().createTimePeriodCosts(500.0, projectResource.getRef(), 10.0);
		TimePeriodCosts timePeriodCosts2 = getProject().createTimePeriodCosts(600.0, projectResource.getRef(), 20.0);
		
		DateUnit dateUnit1 = createSingleYearDateUnit(2008);
		DateUnit dateUnit2 = createSingleYearDateUnit(2009);
		timePeriodCostsMap.add(dateUnit1, timePeriodCosts1);
		timePeriodCostsMap.add(dateUnit2, timePeriodCosts2);
		
		verifyGetTimePeriodCostsForSpecificDateUnit(timePeriodCostsMap, timePeriodCosts1, dateUnit1);
		verifyGetTimePeriodCostsForSpecificDateUnit(timePeriodCostsMap, timePeriodCosts2, dateUnit2);
		
		verifyRolledUpDates(timePeriodCostsMap, "2008-01-01", "2009-12-31");
		
		TimePeriodCosts timePeriodCosts = getProject().createTimePeriodCosts(500.0, projectResource.getRef(), 10.0);
		timePeriodCostsMap.add(new DateUnit(), timePeriodCosts);
		verifyRolledUpDates(timePeriodCostsMap, "2005-01-01", "2011-12-31");
	}

	private void verifyRolledUpDates(TimePeriodCostsMap timePeriodCostsMap, String expectedStartDate, String expectedEndDate) throws Exception
	{
		final DateRange projectStartEndDateRange = getProject().getProjectCalendar().getProjectStartEndDateRange();
		DateRange rolledUpDates = timePeriodCostsMap.getRolledUpDateRange(projectStartEndDateRange);
		assertEquals("wrong rolled up end date? ", expectedStartDate, rolledUpDates.getStartDate().toIsoDateString());
		assertEquals("wrong rolled up end date? ", expectedEndDate, rolledUpDates.getEndDate().toIsoDateString());
	}

	private void verifyGetTimePeriodCostsForSpecificDateUnit(TimePeriodCostsMap timePeriodCostsMap, TimePeriodCosts timePeriodCosts, DateUnit dateUnit)
	{
		TimePeriodCosts foundTimePeriodCosts = timePeriodCostsMap.getTimePeriodCostsForSpecificDateUnit(dateUnit);
		assertEquals("Single TPC wasn't found?", foundTimePeriodCosts, timePeriodCosts);
	}
	
	public void testMergeOverlayWithOverlappingDateUnits() throws Exception
	{
		ProjectResource projectResourcePaul = createProjectResource();
		DateUnit dateUnit2007 = createSingleYearDateUnit(2007);
		DateUnit smallerDateUnit = dateUnit2007.getSubDateUnits().get(0);

		TimePeriodCostsMap timePeriodCostsMap2007 = createTimePeriodCostsMap(dateUnit2007, 22.0, projectResourcePaul, 12.0);
		TimePeriodCostsMap timePeriodCostsMap2007Q1 = createTimePeriodCostsMap(smallerDateUnit, 23.0, projectResourcePaul, 11.0);
		
		verifyMergeOverlay(23, timePeriodCostsMap2007, timePeriodCostsMap2007Q1);
		verifyMergeOverlay(23, timePeriodCostsMap2007Q1, timePeriodCostsMap2007);
	}

	private void verifyMergeOverlay(double expectedExpenses, TimePeriodCostsMap timePeriodCostsMap1, TimePeriodCostsMap timePeriodCostsMap2) throws Exception
	{
		TimePeriodCostsMap overlaidMap = new TimePeriodCostsMap();
		
		overlaidMap.mergeOverlay(timePeriodCostsMap1);
		assertEquals("wrong size after merging map?", 1, overlaidMap.size());
		
		overlaidMap.mergeOverlay(timePeriodCostsMap2);
		assertEquals("wrong size after merging map?", 1, overlaidMap.size());
		
		assertEquals(expectedExpenses, overlaidMap.calculateTimePeriodCosts(new DateUnit("")).getExpense().getValue());
	}

	public void testMergeOverlay() throws Exception
	{	
		DateUnit dateUnit2006 = createSingleYearDateUnit(2006);
		DateUnit dateUnit2007 = createSingleYearDateUnit(2007);
		DateUnit dateUnit2007Q1 = new DateUnit("2007Q1");
		
		ProjectResource projectResourcePaul = createProjectResource();
		
		TimePeriodCostsMap timePeriodCostsMap2006 = new TimePeriodCostsMap();
		TimePeriodCosts timePeriodCosts2006 = updateMapWithNewCreatedTimePeriodCosts(timePeriodCostsMap2006, dateUnit2006, 22.0, projectResourcePaul, 10.0);
		
		TimePeriodCostsMap timePeriodCostsMap2007 = new TimePeriodCostsMap();
		TimePeriodCosts timePeriodCosts2007 = updateMapWithNewCreatedTimePeriodCosts(timePeriodCostsMap2007, dateUnit2007, 22.0, projectResourcePaul, 12.0);
		TimePeriodCosts timePeriodCosts2007Q1 = updateMapWithNewCreatedTimePeriodCosts(timePeriodCostsMap2007, dateUnit2007Q1, 23.0, projectResourcePaul, 11.0);
		
		assertEquals("wrong expense?", 22.0, timePeriodCosts2007.getExpense().getValue());
		assertEquals("wrong calculated project resource?", 120.0 + 22.0, timePeriodCosts2007.calculateTotalCost(getProject()).getValue());
		
		TimePeriodCostsMap projectTimePeriodCostsMap = new TimePeriodCostsMap();
		projectTimePeriodCostsMap.mergeOverlay(timePeriodCostsMap2006);
		assertEquals("wrong content count after merge overlay?", 1, projectTimePeriodCostsMap.size());
		TimePeriodCosts specificTimePeriodCostsFor2006 = projectTimePeriodCostsMap.getTimePeriodCostsForSpecificDateUnit(dateUnit2006);
		assertEquals("Merging larger unit changed existing data?", timePeriodCosts2006, specificTimePeriodCostsFor2006);
		
		projectTimePeriodCostsMap.mergeOverlay(timePeriodCostsMap2007);
		assertEquals("wrong content count after merge overlay?", 2, projectTimePeriodCostsMap.size());
		assertTrue("time period costs map does not contain dateUnit as key?", projectTimePeriodCostsMap.containsSpecificDateUnit(dateUnit2007Q1));
		TimePeriodCosts specificTimePeriodCostsFor2007Q1 = projectTimePeriodCostsMap.getTimePeriodCostsForSpecificDateUnit(dateUnit2007Q1);
		assertEquals("Merging larger unit changed existing data?", timePeriodCosts2007Q1, specificTimePeriodCostsFor2007Q1);
		
		TimePeriodCostsMap timePeriodCostsMapSecond2007Q1 = new TimePeriodCostsMap();
		ProjectResource projectResourceJon = createProjectResource();
		updateMapWithNewCreatedTimePeriodCosts(timePeriodCostsMapSecond2007Q1, dateUnit2007Q1, 25.0, projectResourceJon, 15.0);
		
		projectTimePeriodCostsMap.mergeOverlay(timePeriodCostsMapSecond2007Q1);
		TimePeriodCosts timePeriodCostsAfterOverlay = specificTimePeriodCostsFor2007Q1;
		assertEquals("wrong expense after merge overlay?", (23.0 + 25.0), timePeriodCostsAfterOverlay.getExpense().getValue());
		
		OptionalDouble projectResourceCost = timePeriodCostsAfterOverlay.calculateTotalCost(getProject());
		assertEquals("wrong project resource cost?", (150.0 + 110.0) + 48.0, projectResourceCost.getValue());
	}

	private TimePeriodCosts updateMapWithNewCreatedTimePeriodCosts(TimePeriodCostsMap timePeriodCostsMap, DateUnit dateUnit, double expense, ProjectResource projectResource, double units)
	{
		TimePeriodCosts timePeriodCosts = createTimePeriodCosts(expense, projectResource, units);
		timePeriodCostsMap.add(dateUnit, timePeriodCosts);
		assertEquals(timePeriodCosts, timePeriodCostsMap.getTimePeriodCostsForSpecificDateUnit(dateUnit));
		
		return timePeriodCosts;
	}

	private TimePeriodCosts createTimePeriodCosts(double expenses, ProjectResource projectResource, double units)
	{
		return getProject().createTimePeriodCosts(expenses, projectResource.getRef(), units);
	}

	public void testMergeAddWithEmpty() throws Exception
	{
		ProjectResource resource = createProjectResource();
		double expenses = 1.0;
		double workUnits = 2.0;
		double resourceCosts = workUnits * resource.getCostPerUnit();
		
		TimePeriodCostsMap timePeriodCostsMap = createTimePeriodCostsMap(expenses, resource, workUnits);

		verifyMergeAdd(expenses, resourceCosts, new TimePeriodCostsMap(), timePeriodCostsMap);
		verifyMergeAdd(expenses, resourceCosts, timePeriodCostsMap, new TimePeriodCostsMap());
	}

	public void testMergeAddWithDifferentResrouce() throws Exception
	{
		ProjectResource resource1 = createProjectResource();
		ProjectResource resource2 = createProjectResource();
		double expenses1 = 1.0;
		double expenses2 = 2.0;
		double workUnits1 = 4.0;
		double workUnits2 = 8.0;
		double resourceCosts1 = workUnits1 * resource1.getCostPerUnit();
		double resourceCosts2 = workUnits2 * resource2.getCostPerUnit();

		TimePeriodCostsMap timePeriodCostsMap1 = createTimePeriodCostsMap(expenses1, resource1, workUnits1);
		TimePeriodCostsMap timePeriodCostsMap2 = createTimePeriodCostsMap(expenses2, resource2, workUnits2);
		
		verifyMergeAdd(expenses1+expenses2, resourceCosts1+resourceCosts2, timePeriodCostsMap1, timePeriodCostsMap2);
	}
	
	public void testMergeAddWithSameResource() throws Exception
	{
		ProjectResource resource = createProjectResource();
		double expenses1 = 1.0;
		double expenses2 = 2.0;
		double workUnits1 = 4.0;
		double workUnits2 = 8.0;
		double resourceCosts = (workUnits1 + workUnits2) * resource.getCostPerUnit();

		TimePeriodCostsMap timePeriodCostsMap1 = createTimePeriodCostsMap(expenses1, resource, workUnits1);
		TimePeriodCostsMap timePeriodCostsMap2 = createTimePeriodCostsMap(expenses2, resource, workUnits2);
		
		verifyMergeAdd(expenses1+expenses2, resourceCosts, timePeriodCostsMap1, timePeriodCostsMap2);
	}
	
	public void testMergeAddDifferentDates() throws Exception
	{
		ProjectResource resource = createProjectResource();
		double expenses1 = 1.0;
		double expenses2 = 2.0;
		double workUnits = 4.0;
		double resourceCosts1 = workUnits * resource.getCostPerUnit();

		DateUnit q1 = dateUnit2008.getSubDateUnits().get(0);
		DateUnit q2 = dateUnit2008.getSubDateUnits().get(1);
		TimePeriodCostsMap timePeriodCostsMap1 = createTimePeriodCostsMap(q1, expenses1, resource, workUnits);
		TimePeriodCostsMap timePeriodCostsMap2 = createTimePeriodCostsMap(q2, expenses2);
		TimePeriodCostsMap mergedTimePeriodCostsMap = new TimePeriodCostsMap();
		
		verifyMergeAdd(expenses1, resourceCosts1, mergedTimePeriodCostsMap, timePeriodCostsMap1, q1);
		verifyMergeAdd(expenses2, 0.0, mergedTimePeriodCostsMap, timePeriodCostsMap2, q2);
		assertEquals(expenses1+expenses2, mergedTimePeriodCostsMap.calculateTimePeriodCosts(dateUnit2008).getExpense().getValue());
		assertEquals(workUnits, mergedTimePeriodCostsMap.calculateTimePeriodCosts(dateUnit2008).getUnits(resource.getRef()).getValue());
	}

	public void testMergeAddingIncompletedMaps() throws Exception
	{
		ProjectResource resource = createProjectResource();
		double expenses = 1.0;
		double workUnits = 2.0;
		double resourceCosts = workUnits * resource.getCostPerUnit();

		TimePeriodCostsMap mapWithOnlyExpenses = createTimePeriodCostsMap(dateUnit2008, expenses);
		TimePeriodCostsMap mapWithOnlyResourceWorkUnits = createTimePeriodCostsMap(dateUnit2008, resource.getRef(), workUnits);
		
		verifyMergeAdd(expenses, resourceCosts, mapWithOnlyExpenses, mapWithOnlyResourceWorkUnits);
	}
	
	private void verifyMergeAdd(double expectedExpense, double expectedResourcesCost, TimePeriodCostsMap destinationTimePeriodCostsMap, TimePeriodCostsMap timePeriodCostsMapToBeMerged) throws Exception
	{
		verifyMergeAdd(expectedExpense,expectedResourcesCost, destinationTimePeriodCostsMap, timePeriodCostsMapToBeMerged,dateUnit2008);
	}

	private void verifyMergeAdd(double expectedExpense, double expectedResourcesCost, TimePeriodCostsMap destinationTimePeriodCostsMap, TimePeriodCostsMap timePeriodCostsMapToBeMerged, DateUnit dateUnitForTimePeriodCosts) throws Exception
	{
		double expectedTotalCost = expectedExpense + expectedResourcesCost;
		destinationTimePeriodCostsMap.mergeAll(timePeriodCostsMapToBeMerged);
		TimePeriodCosts  foundTimePeriodCosts = destinationTimePeriodCostsMap.calculateTimePeriodCosts(dateUnitForTimePeriodCosts);
		assertEquals("wrong expense after merge?", expectedExpense, foundTimePeriodCosts.getExpense().getValue());
		assertEquals("wrong total cost after merge?", expectedTotalCost, foundTimePeriodCosts.calculateTotalCost(getProject()).getValue());
	}
	
	public void testFilterByProjectResource() throws Exception
	{
		TimePeriodCostsMap timePeriodCostsMap = new TimePeriodCostsMap();
		assertTrue("should not have any project resources?", timePeriodCostsMap.getAllProjectResourceRefs().isEmpty());
		
		DateUnit dateUnit2006Q1 = new DateUnit("2006Q1");
		DateUnit dateUnit2006Q2 = new DateUnit("2006Q2");

		ProjectResource projectResourcePaul = createProjectResource();
		ProjectResource projectResourceJill = createProjectResource();
		
		TimePeriodCosts timePeriodCosts2006WithPaul = updateMapWithNewCreatedTimePeriodCosts(timePeriodCostsMap, dateUnit2006Q1, 22.0, projectResourcePaul, 10.0);
		TimePeriodCosts timePeriodCosts2006WithJill = updateMapWithNewCreatedTimePeriodCosts(timePeriodCostsMap, dateUnit2006Q2, 12.0, projectResourceJill, 1.0);
		
		timePeriodCostsMap.add(dateUnit2006Q1, timePeriodCosts2006WithPaul);
		timePeriodCostsMap.add(dateUnit2006Q2, timePeriodCosts2006WithJill);
		
		assertEquals("wrong project resource count?", 2, timePeriodCostsMap.getAllProjectResourceRefs().size());
		
		timePeriodCostsMap.filterByProjectResource(projectResourceJill.getRef());
		
		ORefSet projectResourceRefs = timePeriodCostsMap.getAllProjectResourceRefs();
		assertEquals("map was not filtered properly?", 1, projectResourceRefs.size());
		assertTrue("wrong project resource in map?", projectResourceRefs.contains(projectResourceJill.getRef()));
	}
	
	//FIXME urgent - finish this test and add verify calls
	public void NONWORKINGtestMergeNonConflictingExpenses() throws Exception
	{
		DateUnit dateUnitTotal = new DateUnit("");
		DateUnit dateUnit2007 = createSingleYearDateUnit(2007);
		DateUnit dateUnit2002 = createSingleYearDateUnit(2002);
		
		verifyMergeNonConflicting(dateUnitTotal, 2.0, 1.0, dateUnit2002, 1.0, 1.0);
		verifyMergeNonConflicting(dateUnit2007, 1.0, 1.0, dateUnit2002, 2.0, 2.0);
		verifyMergeNonConflicting(dateUnit2007, 1.0, 1.0, dateUnitTotal, 2.0, 2.0);
	}
	
	private void verifyMergeNonConflicting(DateUnit strategyDateUnit, double strategyExpense, double expectedExpenseForStrategyDateUnit, DateUnit activityDateUnit, double activityExpense, double expectedExpenseForActivityDateUnit)
	{
		TimePeriodCostsMap strategyTimePeriodCostsMap = createTimePeriodCostsMap(strategyDateUnit, strategyExpense);
		TimePeriodCostsMap activityTimePeriodCostsMap = createTimePeriodCostsMap(activityDateUnit, activityExpense);
		
		activityTimePeriodCostsMap.mergeNonConflicting(strategyTimePeriodCostsMap);
		TimePeriodCosts timePeriodCostsForTotal = activityTimePeriodCostsMap.getTimePeriodCostsForSpecificDateUnit(strategyDateUnit);
		assertEquals("wrong expense after merge?", expectedExpenseForStrategyDateUnit, timePeriodCostsForTotal.getExpense());
			
		TimePeriodCosts timePeriodCostsFor2002 = activityTimePeriodCostsMap.getTimePeriodCostsForSpecificDateUnit(activityDateUnit);
		assertEquals("wrong expense after merge?", expectedExpenseForActivityDateUnit, timePeriodCostsFor2002.getExpense());
	}

	private TimePeriodCostsMap createTimePeriodCostsMap(double expense, ProjectResource projectResource, double units)
	{
		return createTimePeriodCostsMap(dateUnit2008, expense, projectResource, units);
	}
	
	private TimePeriodCostsMap createTimePeriodCostsMap(DateUnit dateUnitToUse, double expense, ProjectResource projectResource, double units)
	{
		return createTimePeriodCostsMap(dateUnitToUse, expense, projectResource.getRef(), units);
	}
	
	private TimePeriodCostsMap createTimePeriodCostsMap(DateUnit dateUnitToUse, double expense, ORef projectResourceRef, double units)
	{
		TimePeriodCosts timePeriodCosts = getProject().createTimePeriodCosts(expense, projectResourceRef, units);
		TimePeriodCostsMap timePeriodCostsMap = new TimePeriodCostsMap();
		timePeriodCostsMap.add(dateUnitToUse, timePeriodCosts);
		
		return timePeriodCostsMap;
	}
	
	private TimePeriodCostsMap createTimePeriodCostsMap(DateUnit dateUnitToUse, ORef projectResourceRef, double units)
	{
		TimePeriodCosts timePeriodCosts = getProject().createTimePeriodCosts(projectResourceRef, units);
		TimePeriodCostsMap timePeriodCostsMap = new TimePeriodCostsMap();
		timePeriodCostsMap.add(dateUnitToUse, timePeriodCosts);
		
		return timePeriodCostsMap;
	}
	
	private TimePeriodCostsMap createTimePeriodCostsMap(DateUnit dateUnitToUse, double expense)
	{
		TimePeriodCostsMap timePeriodCostsMap = new TimePeriodCostsMap();
		TimePeriodCosts timePeriodCosts = getProject().createTimePeriodCosts(expense);
		timePeriodCostsMap.add(dateUnitToUse, timePeriodCosts);
		
		return timePeriodCostsMap;
	}
		
	private ProjectResource createProjectResource() throws Exception
	{
		ProjectResource projectResource = getProject().createAndPopulateProjectResource();
		getProject().fillCostPerUnitField(projectResource, "10");
		return projectResource;
	}	

	private DateUnit createSingleYearDateUnit(int year) throws Exception
	{
		return getProject().createSingleYearDateUnit(year);
	}

	private DateUnit dateUnit2008;
}
