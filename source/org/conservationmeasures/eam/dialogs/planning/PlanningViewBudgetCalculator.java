/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs.planning;

import java.util.Vector;

import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.project.ProjectCalendar;
import org.conservationmeasures.eam.utils.DateRange;

public class PlanningViewBudgetCalculator
{
	public PlanningViewBudgetCalculator(Project projectToUse) throws Exception
	{
		totalCalculator = new PlanniningViewBudgetTotalsCalculator(projectToUse);
		yearlyDateRanges = new ProjectCalendar(projectToUse).getYearlyDateRanges();
	
		combineAllDateRangesIntoOne();
	}
	
	private void combineAllDateRangesIntoOne() throws Exception
	{
		DateRange startDateRange = (DateRange)yearlyDateRanges.get(0);
		DateRange endDateRange = (DateRange)yearlyDateRanges.get(yearlyDateRanges.size() - 1);
		combinedDataRange = DateRange.combine(startDateRange, endDateRange);
	}
	
	public double getTotal(ORef ref)
	{
       return totalCalculator.calculateTotalCost(ref, combinedDataRange);
	}
	
	private DateRange combinedDataRange;
	private Vector yearlyDateRanges;
	private PlanniningViewBudgetTotalsCalculator totalCalculator;	
}
