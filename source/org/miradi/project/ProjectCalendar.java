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
package org.miradi.project;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Vector;

import org.martus.util.MultiCalendar;
import org.miradi.commands.CommandSetObjectData;
import org.miradi.main.CommandExecutedEvent;
import org.miradi.main.CommandExecutedListener;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.DateUnit;
import org.miradi.objects.ProjectMetadata;
import org.miradi.questions.ChoiceItem;
import org.miradi.questions.ChoiceQuestion;
import org.miradi.questions.MonthAbbreviationsQuestion;
import org.miradi.schemas.ProjectMetadataSchema;
import org.miradi.utils.DateRange;

public class ProjectCalendar implements CommandExecutedListener
{
	public ProjectCalendar(Project projectToUse) throws Exception
	{
		project = projectToUse;
	}
	
	public void enable()
	{
		getProject().addCommandExecutedListener(this);
	}
	
	public void disable()
	{
		getProject().removeCommandExecutedListener(this);
	}
	
	public String getLongDateUnitString(DateUnit dateUnit)
	{
		if (dateUnit.isProjectTotal())
			return EAM.text("Total");
		
		if (dateUnit.isYear())
			return getYearString(dateUnit, getFiscalYearFirstMonth());
		
		if (dateUnit.isQuarter())
			return getLongQuarterString(dateUnit, getFiscalYearFirstMonth());

		if (dateUnit.isMonth())
			return getLongMonthString(dateUnit);
		
		if (dateUnit.isDay())
			return Integer.toString(dateUnit.getDay());
		
		throw new RuntimeException("DateUnit could not be converted to long string. DateUnit = " + dateUnit + ".  Fiscal Year First Month = " + getFiscalYearFirstMonth());
	}

	public String getShortDateUnitString(DateUnit dateUnit)
	{
		return getShortDateUnit(dateUnit, getFiscalYearFirstMonth());
	}
	
	public String getDateRangeName(DateRange dateRange)
	{
		return getFullDateRangeString(dateRange, getFiscalYearFirstMonth());
	}
	
	public int getFiscalYearFirstMonth()
	{
		return getProject().getMetadata().getFiscalYearFirstMonth();
	}

	public MultiCalendar getPlanningStartMultiCalendar()
	{
		return MultiCalendar.createFromIsoDateString(getPlanningStartDate());
	}
	
	public MultiCalendar getPlanningEndMultiCalendar()
	{
		return MultiCalendar.createFromIsoDateString(getPlanningEndDate());
	}
	
	public String getPlanningStartDate()
	{
		MultiCalendar now = new MultiCalendar();
		MultiCalendar startOfCalendarYear = MultiCalendar.createFromGregorianYearMonthDay(now.getGregorianYear(), getFiscalYearFirstMonth(), 1);

		ProjectMetadata metadata = project.getMetadata();
		String candidatesBestFirst[] = new String[] {
			metadata.getWorkPlanStartDateAsString(),
			metadata.getStartDate(),
			startOfCalendarYear.toIsoDateString(),
		};
		
		return firstNonBlank(candidatesBestFirst);
	}

	public String getPlanningEndDate()
	{
		MultiCalendar now = new MultiCalendar();
		MultiCalendar planningStartMultiCalendar = MultiCalendar.createFromGregorianYearMonthDay(now.getGregorianYear(), getFiscalYearFirstMonth(), 1);
		MultiCalendar endOfCalendarYear = getOneYearLater(planningStartMultiCalendar);
		endOfCalendarYear.addDays(-1);

		ProjectMetadata metadata = project.getMetadata();
		String candidatesBestFirst[] = new String[] {
			metadata.getWorkPlanEndDate(),
			metadata.getExpectedEndDate(),
			endOfCalendarYear.toIsoDateString(),
		};
		
		return firstNonBlank(candidatesBestFirst);
	}

	private String firstNonBlank(String[] candidatesBestFirst)
	{
		for(String candidate : candidatesBestFirst)
		{
			if(candidate.length() != 0)
				return candidate;
		}
		
		throw new RuntimeException("All candidate strings were blank");
	}
	
	public void commandExecuted(CommandExecutedEvent event)
	{
		if(!event.isSetDataCommand())
			return;
		
		CommandSetObjectData cmd = (CommandSetObjectData) event.getCommand();
		if(cmd.getObjectType() != ProjectMetadataSchema.getObjectType())
			return;
		
		try
		{
			if(cmd.getFieldTag().equals(ProjectMetadata.TAG_START_DATE) ||
					cmd.getFieldTag().equals(ProjectMetadata.TAG_EXPECTED_END_DATE) ||
					cmd.getFieldTag().equals(ProjectMetadata.TAG_FISCAL_YEAR_START) ||
					cmd.getFieldTag().equals(ProjectMetadata.TAG_WORKPLAN_TIME_UNIT))
			{
			}
		}
		catch(Exception e)
		{
			EAM.panic(e);
		}
	}

	private Project getProject()
	{
		return project;
	}

	private static int getFiscalYearMonthSkew(int fiscalYearFirstMonth)
	{
		switch(fiscalYearFirstMonth)
		{
			case 1: return 0;
			case 4: return 3;
			case 7: return -6;
			case 10: return -3;
		}
		
		throw new RuntimeException("Unknown fiscal year month start: " + fiscalYearFirstMonth);
	}

	public static String getShortDateUnit(DateUnit dateUnit, int fiscalYearFirstMonth)
	{
		if (dateUnit.isProjectTotal())
			return EAM.text("Total");
		
		if (dateUnit.isYear())
			return getYearString(dateUnit, fiscalYearFirstMonth);
		
		if (dateUnit.isQuarter())
			return getQuarterString(dateUnit, fiscalYearFirstMonth);

		if (dateUnit.isMonth())
			return getMonthString(dateUnit);
		
		if (dateUnit.isDay())
			return Integer.toString(dateUnit.getDay());
		
		throw new RuntimeException("DateUnit could not be converted to string. DateUnit = " + dateUnit + ".  Fiscal Year First Month = " + fiscalYearFirstMonth);
	}

	private static String getMonthString(DateUnit dateUnit)
	{
		ChoiceQuestion question = new MonthAbbreviationsQuestion();
		String month = Integer.toString(dateUnit.getMonth());
		ChoiceItem choiceItem = question.findChoiceByCode(month);
		return choiceItem.getLabel();
	}
	
	private static String getQuarterString(DateUnit dateUnit, int fiscalYearFirstMonth)
	{
		int quarter = dateUnit.getQuarter();
		int startFiscalQuarter = (fiscalYearFirstMonth - 1 ) / 3 + 1;
		int fiscalYearQuarter = ((quarter - startFiscalQuarter) + 4) % 4 + 1;
		String quarterlyPrefixString = getQuarterlyPrefixString(); 
		if (fiscalYearFirstMonth > 1)
			quarterlyPrefixString = getFiscalQuarterlyPrefixString();
		
		return quarterlyPrefixString + fiscalYearQuarter;
	}
	
	private static String getLongQuarterString(DateUnit dateUnit, int fiscalYearFirstMonth)
	{
		DateUnit yearDateUnit = dateUnit.getSuperDateUnit(fiscalYearFirstMonth);
		String yearString = getYearString(yearDateUnit, fiscalYearFirstMonth);
		String quarterString = getQuarterString(dateUnit, fiscalYearFirstMonth);
		HashMap<String, String> tokenReplacementMap = new HashMap<String, String>();
		tokenReplacementMap.put("%year", yearString);
		tokenReplacementMap.put("%quarter", quarterString);
		return EAM.substitute("%year - %quarter", tokenReplacementMap);
	}
	
	private static String getLongMonthString(DateUnit dateUnit)
	{
		String yearString = Integer.toString(dateUnit.getYear());
		String monthString = getMonthString(dateUnit);
		HashMap<String, String> tokenReplacementMap = new HashMap<String, String>();
		tokenReplacementMap.put("%year", yearString);
		tokenReplacementMap.put("%month", monthString);
		return EAM.substitute("%year - %month", tokenReplacementMap);
	}
	
	private static String getYearString(DateUnit dateUnit, int fiscalYearFirstMonth)
	{
		String yearString = dateUnit.getYearYearString();
		int yearStartMonth = dateUnit.getYearStartMonth();
		if (yearStartMonth == 1)
			return yearString;
		
		int year = Integer.parseInt(yearString);
		int fiscalYear = year + 1;
		if (yearStartMonth == fiscalYearFirstMonth)
			return getFiscalYearString(fiscalYear);
		
		return Integer.toString(year) + "-" +Integer.toString(fiscalYear);
	}

	public static String getFullDateRangeString(DateRange dateRange, int fiscalYearFirstMonth)
	{
		String fullRange = dateRange.toString();
		
		MultiCalendar startDate = dateRange.getStartDate();
		MultiCalendar afterEndDate = new MultiCalendar(dateRange.getEndDate());
		afterEndDate.addDays(1);
		
		if(startDate.getGregorianDay() != 1)
			return fullRange;
		if(afterEndDate.getGregorianDay() != 1)
			return fullRange;
		
		int skew = ProjectCalendar.getFiscalYearMonthSkew(fiscalYearFirstMonth);
		
		int startFiscalMonth = startDate.getGregorianMonth();
		if((startFiscalMonth % 3) != 1)
			return fullRange;
	
		int endFiscalMonth = afterEndDate.getGregorianMonth();
		if((endFiscalMonth % 3) != 1)
			return fullRange;
	
		int startFiscalYear = startDate.getGregorianYear();
		startFiscalMonth -= skew;
		while(startFiscalMonth < 1)
		{
			startFiscalMonth += 12;
			--startFiscalYear;
		}
		while(startFiscalMonth > 12)
		{
			startFiscalMonth -= 12;
			++startFiscalYear;
		}
		
		int endFiscalYear = afterEndDate.getGregorianYear();
		endFiscalMonth -= skew;
		while(endFiscalMonth < 1)
		{
			endFiscalMonth += 12;
			--endFiscalYear;
		}
		while(endFiscalMonth > 12)
		{
			endFiscalMonth -= 12;
			++endFiscalYear;
		}
		
		String startYearString = getFiscalYearString(startFiscalYear);
		
		if(startFiscalYear+1 == endFiscalYear && startFiscalMonth == endFiscalMonth && startFiscalMonth == 1)
			return startYearString;
		
		String endYearString = getFiscalYearString(endFiscalYear);
		
		int startFiscalQuarter = (startFiscalMonth-1) / 3 + 1;
		int endFiscalQuarter = (endFiscalMonth - 1) / 3;
		if (endFiscalQuarter == 0)
		{
			endFiscalQuarter = 4;
		}
		
		if(startFiscalQuarter == 4)
		{
			if(startFiscalYear+1 != endFiscalYear)
				return fullRange;
		}
		else if(startFiscalYear != endFiscalYear)
		{
			return fullRange;
		}
		
		String firstFiscalQuarter = getQuarterlyPrefixString() + startFiscalQuarter + " " + startYearString;
		if (startFiscalQuarter == endFiscalQuarter)
			return firstFiscalQuarter;
		
		return firstFiscalQuarter + " - " + getQuarterlyPrefixString() + endFiscalQuarter + " " + endYearString;
	}

	private static String getFiscalYearString(int fiscalYear)
	{
		String yearString = Integer.toString(fiscalYear);
		return EAM.text("Fiscal Year|FY") + yearString.substring(2);
	}
	
	private static String getQuarterlyPrefixString()
	{
		return EAM.text("Quarter Prefix|Q");
	}
	
	private static String getFiscalQuarterlyPrefixString()
	{
		return EAM.text("Fiscal Quarter Prefix|FQ");
	}
	
	public DateRange convertToDateRange(DateUnit dateUnit) throws Exception
	{
		if(dateUnit.isProjectTotal())
			return getProjectPlanningDateRange();
		
		return dateUnit.asDateRange();
	}
	
	public boolean hasSubDateUnits(DateUnit dateUnit) throws Exception
	{
		return getSubDateUnits(dateUnit).size() > 0;
	}
	
	public Vector<DateUnit> getSuperDateUnitsHierarchy(DateUnit dateUnit)
	{
		Vector<DateUnit> superDateUnits = dateUnit.getSuperDateUnitHierarchy(getFiscalYearFirstMonth());
		if (shouldHideQuarterColumns())
			return removeQuarterDateUnits(superDateUnits);
		
		return superDateUnits;
	}
	
	public Vector<DateUnit> getSubDateUnits(DateUnit dateUnit) throws Exception
	{
		Vector<DateUnit> subDateUnits = getSubDateUnitsWithinProjectPlanningDates(dateUnit);
		if (dateUnit.isYear() && shouldHideQuarterColumns())
			return getMonthAsSubDateUnitsOfYear(subDateUnits);
		
		return subDateUnits;
	}
	
	public Vector<DateUnit> getAllProjectYearDateUnits() throws Exception
	{
		return getSubDateUnits(new DateUnit());
	}
	
	public Vector<DateUnit> getAllProjectQuarterDateUnits() throws Exception
	{
		if(shouldHideQuarterColumns())
			return new Vector<DateUnit>();
		
		return getAllSubDateUnits(getAllProjectYearDateUnits());
	}
	
	public Vector<DateUnit> getAllProjectMonthDateUnits() throws Exception
	{
		if(shouldHideQuarterColumns())
			return getAllSubDateUnits(getAllProjectYearDateUnits());

		return getAllSubDateUnits(getAllProjectQuarterDateUnits());
	}

	private Vector<DateUnit> getAllSubDateUnits(Vector<DateUnit> dateUnits) throws Exception
	{
		Vector<DateUnit> subDateUnits = new Vector<DateUnit>();
		for(DateUnit dateUnit : dateUnits)
		{
			subDateUnits.addAll(getSubDateUnits(dateUnit));
		}

		return subDateUnits;
	}
	
	private Vector<DateUnit> removeQuarterDateUnits(Vector<DateUnit> superDateUnits)
	{
		Vector<DateUnit> withoutQuarters = new Vector<DateUnit>();
		for(DateUnit superDateUnit : superDateUnits)
		{
			if (!superDateUnit.isQuarter())
				withoutQuarters.add(superDateUnit);
		}
		
		return withoutQuarters;
	}

	private Vector<DateUnit> getMonthAsSubDateUnitsOfYear(Vector<DateUnit> quarterDateUnits) throws Exception
	{
		LinkedHashSet<DateUnit> monthDateUnits = new LinkedHashSet<DateUnit>();
		for(DateUnit quarterDateUnit : quarterDateUnits)
		{
			Vector<DateUnit> subMonthDateUnitsOfQuarterDateUnit = getSubDateUnitsWithinProjectPlanningDates(quarterDateUnit);
			monthDateUnits.addAll(subMonthDateUnitsOfQuarterDateUnit);
		}
		
		return new Vector<DateUnit>(monthDateUnits);
	}

	private Vector<DateUnit> getSubDateUnitsWithinProjectPlanningDates(DateUnit dateUnit) throws Exception
	{
		return getSubDateUnitsWithin(dateUnit, getProjectPlanningDateRange());
	}

	public Vector<DateUnit> getSubDateUnitsWithin(DateUnit dateUnit, DateRange projectDateRange) throws Exception
	{
		if (dateUnit.isProjectTotal())
			return getProjectYearsDateUnits(projectDateRange);
		
		if (dateUnit.hasSubDateUnits())
			return dateUnit.getSubDateUnitsWithin(projectDateRange);
		
		return new Vector<DateUnit>();
	}
	
	private boolean shouldHideQuarterColumns()
	{
		return !shouldShowQuarterColumns();
	}

	public boolean shouldShowQuarterColumns()
	{
		return getProject().getMetadata().areQuarterColumnsVisible();
	}

	public Vector<DateUnit> getProjectYearsDateUnits(DateRange dateRange)
	{
		Vector<DateUnit> dateUnits = new Vector<DateUnit>();
		MultiCalendar start = dateRange.getStartDate();
		MultiCalendar end = dateRange.getEndDate();

		MultiCalendar startOfFiscalYear = getStartOfFiscalYearContaining(start);
		while(startOfFiscalYear.before(end))
		{
			DateUnit year = DateUnit.createFiscalYear(startOfFiscalYear.getGregorianYear(), startOfFiscalYear.getGregorianMonth());
			dateUnits.add(year);
			startOfFiscalYear = getOneYearLater(startOfFiscalYear);
		}
		
		return dateUnits;
	}

	public MultiCalendar getStartOfFiscalYearContaining(MultiCalendar start)
	{
		int startYear = start.getGregorianYear();
		int startMonth = getFiscalYearFirstMonth();
		MultiCalendar startOfFiscalYear = MultiCalendar.createFromGregorianYearMonthDay(startYear, startMonth, 1);
		while(startOfFiscalYear.after(start))
			startOfFiscalYear = getOneYearEarlier(startOfFiscalYear);

		return startOfFiscalYear;
	}
	
	public DateRange getProjectPlanningDateRange() throws Exception
	{
		MultiCalendar thisStartDate = getPlanningStartMultiCalendar();
		MultiCalendar thisEndDate = getPlanningEndMultiCalendar();
		
		if (thisStartDate.after(thisEndDate))
		{
			EAM.logWarning("Project planning DateRange end date: " + thisEndDate + " was before start date: " + thisEndDate);
			return new DateRange(thisStartDate, thisStartDate);
		}
			
		return new DateRange(thisStartDate, thisEndDate);
	}
	
	public boolean arePlanningStartAndEndDatesFlipped()
	{
		return getPlanningStartMultiCalendar().after(getPlanningEndMultiCalendar());
	}
	
	public DateUnit getProjectPlanningDateUnit() throws Exception
	{		
		return DateUnit.createFromDateRange(getProjectPlanningDateRange());
	}

	private MultiCalendar getOneYearLater(MultiCalendar startOfFiscalYear)
	{
		return getShiftedByYears(startOfFiscalYear, 1);
	}

	private MultiCalendar getOneYearEarlier(MultiCalendar startOfFiscalYear)
	{
		return getShiftedByYears(startOfFiscalYear, -1);
	}

	private MultiCalendar getShiftedByYears(MultiCalendar startOfFiscalYear,
			int delta)
	{
		return MultiCalendar.createFromGregorianYearMonthDay(
				startOfFiscalYear.getGregorianYear() + delta, 
				startOfFiscalYear.getGregorianMonth(), 
				startOfFiscalYear.getGregorianDay());
	}

	public String convertToSafeString(DateRange combinedDateRange)
	{
		if (combinedDateRange == null)
			return "";
		
		return  getDateRangeName(combinedDateRange);
	}

	private Project project;
}
