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

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

import org.martus.util.MultiCalendar;
import org.miradi.main.TestCaseWithProject;
import org.miradi.objecthelpers.DateUnit;
import org.miradi.objects.ProjectMetadata;
import org.miradi.utils.DateRange;

public class TestProjectCalendar extends TestCaseWithProject
{
	public TestProjectCalendar(String name)
	{
		super(name);
	}
	
	public void testGettingDatesThatAreNotSet() throws Exception
	{
		ProjectCalendar pc = getProjectCalendar();
		
		MultiCalendar startDate = MultiCalendar.createFromIsoDateString(pc.getPlanningStartDate());
		assertEquals(1, startDate.getGregorianMonth());
		assertEquals(1, startDate.getGregorianDay());
		MultiCalendar endDate = MultiCalendar.createFromIsoDateString(pc.getPlanningEndDate());
		assertEquals(12, endDate.getGregorianMonth());
		assertEquals(31, endDate.getGregorianDay());
	}

	public void testGetFiscalYearQuarterName() throws Exception
	{
		verifyFiscalQuarterName("FY06", "2006-01-01", "2006-12-31", 1);
		verifyFiscalQuarterName("Q1 FY06", "2006-01-01", "2006-03-31", 1);
		verifyFiscalQuarterName("Q2 FY06", "2006-04-01", "2006-06-30", 1);
		verifyFiscalQuarterName("Q3 FY06", "2006-07-01", "2006-09-30", 1);
		verifyFiscalQuarterName("Q4 FY06", "2006-10-01", "2006-12-31", 1);
		
		verifyFiscalQuarterName("FY06", "2006-04-01", "2007-03-31", 4);
		verifyFiscalQuarterName("Q1 FY06", "2006-04-01", "2006-06-30", 4);
		verifyFiscalQuarterName("Q2 FY06", "2006-07-01", "2006-09-30", 4);
		verifyFiscalQuarterName("Q3 FY06", "2006-10-01", "2006-12-31", 4);
		verifyFiscalQuarterName("Q4 FY06", "2007-01-01", "2007-03-31", 4);

		verifyFiscalQuarterName("FY06", "2005-07-01", "2006-06-30", 7);
		verifyFiscalQuarterName("Q1 FY06", "2005-07-01", "2005-09-30", 7);
		verifyFiscalQuarterName("Q2 FY06", "2005-10-01", "2005-12-31", 7);
		verifyFiscalQuarterName("Q3 FY06", "2006-01-01", "2006-03-31", 7);
		verifyFiscalQuarterName("Q4 FY06", "2006-04-01", "2006-06-30", 7);

		verifyFiscalQuarterName("FY06", "2005-10-01", "2006-09-30", 10);
		verifyFiscalQuarterName("Q1 FY06", "2005-10-01", "2005-12-31", 10);
		verifyFiscalQuarterName("Q2 FY06", "2006-01-01", "2006-03-31", 10);
		verifyFiscalQuarterName("Q3 FY06", "2006-04-01", "2006-06-30", 10);
		verifyFiscalQuarterName("Q4 FY06", "2006-07-01", "2006-09-30", 10);

		verifyFiscalQuarterName("Q1 FY06 - Q3 FY06", "2005-07-01", "2006-03-31", 7);
		verifyFiscalQuarterName("Q4 FY06 - Q1 FY07", "2006-04-01", "2006-09-30", 7);

		verifyFiscalQuarterName("2006", "2006-01-01", "2006-12-31", 10);
	}

	
	private void verifyFiscalQuarterName(String expectedName, String beginDate, String endDate, int fiscalYearFirstMonth) throws Exception
	{
		MultiCalendar begin = getProject().parseIsoDate(beginDate);
		MultiCalendar end = getProject().parseIsoDate(endDate);
		DateRange dateRange = new DateRange(begin, end);
		String result = ProjectCalendar.getFiscalYearQuarterName(dateRange, fiscalYearFirstMonth);
		assertEquals(expectedName, result);
	}
	
	public void testWithYearDateUnit() throws Exception
	{
		DateUnit dateUnit = new DateUnit("YEARFROM:2006-01");
		DateRange dateRange = getProjectCalendar().convertToDateRange(dateUnit);
		assertEquals("wrong start date?", "2006-01-01", dateRange.getStartDate().toIsoDateString());
		assertEquals("wrong end date?", "2006-12-31", dateRange.getEndDate().toIsoDateString());
	}

	public void testBlankDateUnit() throws Exception
	{
		MultiCalendar startDate = getProject().parseIsoDate("2006-01-02");
		MultiCalendar endDate = getProject().parseIsoDate("2007-01-02");
		
		getProject().getMetadata().setData(ProjectMetadata.TAG_START_DATE, startDate.toIsoDateString());
		getProject().getMetadata().setData(ProjectMetadata.TAG_EXPECTED_END_DATE, endDate.toIsoDateString());
		DateUnit blankDateUnit = new DateUnit();
		DateRange dateRange = getProjectCalendar().convertToDateRange(blankDateUnit);
		
		MultiCalendar expectedStartYear = MultiCalendar.createFromGregorianYearMonthDay(2006, 1, 1);
		MultiCalendar expectedEndYear = MultiCalendar.createFromGregorianYearMonthDay(2007, 12, 31);
		DateRange expectedDateRange = new DateRange(expectedStartYear, expectedEndYear);
		assertEquals("date ranges do not match?", expectedDateRange, dateRange);
	}

	public void testWithBadDateData()
	{
		try
		{
			DateUnit bogusDateUnit = new DateUnit("bogusDate");
			getProjectCalendar().convertToDateRange(bogusDateUnit);
			fail("should have thrown an exception when trying to convert invalid data?");
		}
		catch (Exception ignoreExpected)
		{
		}
	}
	
	public void testGetSubDateUnits() throws Exception
	{
		MultiCalendar startDate = getProject().parseIsoDate("2006-01-02");
		MultiCalendar endDate = getProject().parseIsoDate("2007-01-02");
		
		getProject().getMetadata().setData(ProjectMetadata.TAG_START_DATE, startDate.toIsoDateString());
		getProject().getMetadata().setData(ProjectMetadata.TAG_EXPECTED_END_DATE, endDate.toIsoDateString());
		
		DateUnit blankDateUnit = new DateUnit();
		Vector<DateUnit> subDateUnits = getProjectCalendar().getSubDateUnits(blankDateUnit);
		assertEquals("wrong sub date units count?", 2, subDateUnits.size());
		assertTrue("does not contain date?", subDateUnits.contains(DateUnit.createFiscalYear(2006, 1)));
		assertTrue("does not contain date?", subDateUnits.contains(DateUnit.createFiscalYear(2007, 1)));
	}
	
	public void testMultiCalendarBefore1970() throws Exception
	{
		MultiCalendar ancient = MultiCalendar.createFromGregorianYearMonthDay(1919, 1, 9);
		assertEquals("1919-01-09", ancient.toIsoDateString());
		ancient.addDays(1);
		assertEquals("1919-01-10", ancient.toIsoDateString());
		Date date = ancient.getTime();
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		MultiCalendar fromDate = new MultiCalendar(cal);
		assertEquals("1919-01-10", fromDate.toIsoDateString());
		
	}
	
	public void testGetStartOfFiscalYearContaining() throws Exception
	{
		MultiCalendar feb2008 = MultiCalendar.createFromGregorianYearMonthDay(2008, 2, 1);
		
		MultiCalendar jan2008 = MultiCalendar.createFromGregorianYearMonthDay(2008, 1, 1);
		MultiCalendar april2007 = MultiCalendar.createFromGregorianYearMonthDay(2007, 4, 1);
		MultiCalendar july2007 = MultiCalendar.createFromGregorianYearMonthDay(2007, 7, 1);
		MultiCalendar oct2007 = MultiCalendar.createFromGregorianYearMonthDay(2007, 10, 1);
		
		assertEquals(jan2008, getProjectCalendar().getStartOfFiscalYearContaining(feb2008));

		setFiscalYearStartMonth(4);
		assertEquals(april2007, getProjectCalendar().getStartOfFiscalYearContaining(feb2008));

		setFiscalYearStartMonth(7);
		assertEquals(july2007, getProjectCalendar().getStartOfFiscalYearContaining(feb2008));

		setFiscalYearStartMonth(10);
		assertEquals(oct2007, getProjectCalendar().getStartOfFiscalYearContaining(feb2008));
	}

	private void setFiscalYearStartMonth(int startMonth) throws Exception
	{
		getProject().getMetadata().setData(ProjectMetadata.TAG_FISCAL_YEAR_START, Integer.toString(startMonth));
	}
	
	public void testGetProjectStartEndDateUnits() throws Exception
	{
		MultiCalendar start2008 = MultiCalendar.createFromGregorianYearMonthDay(2008, 1, 1);
		MultiCalendar end2009 = MultiCalendar.createFromGregorianYearMonthDay(2009, 12, 31);
		DateRange twoCalendarYears = new DateRange(start2008, end2009);
		Vector<DateUnit> twoYears = getProjectCalendar().getProjectYearsDateUnits(twoCalendarYears);
		assertEquals(2, twoYears.size());
		assertEquals(DateUnit.createFiscalYear(2008, 1), twoYears.get(0));
		assertEquals(DateUnit.createFiscalYear(2009, 1), twoYears.get(1));
		
		setFiscalYearStartMonth(7);
		MultiCalendar startMid2006 = MultiCalendar.createFromGregorianYearMonthDay(2006, 7, 1);
		MultiCalendar endMid2009 = MultiCalendar.createFromGregorianYearMonthDay(2009, 6, 30);
		DateRange threeFiscalYears = new DateRange(startMid2006, endMid2009);
		Vector<DateUnit> threeYears = getProjectCalendar().getProjectYearsDateUnits(threeFiscalYears);
		assertEquals(3, threeYears.size());
		assertEquals(DateUnit.createFiscalYear(2006, 7), threeYears.get(0));
		assertEquals(DateUnit.createFiscalYear(2007, 7), threeYears.get(1));
		assertEquals(DateUnit.createFiscalYear(2008, 7), threeYears.get(2));
	}
	
	public void testGetShortDateUnitString()
	{
		int FISCAL_YEAR_START_JAN = 1;
		int FISCAL_YEAR_START_APR = 4;
		int FISCAL_YEAR_START_JUL = 7;
		int FISCAL_YEAR_START_OCT = 10;

		DateUnit totalDateUnit = new DateUnit();
		assertEquals("wrong string for total (fiscal year jan.)?", "Total", getProjectCalendar().getShortDateUnit(totalDateUnit, FISCAL_YEAR_START_JAN));
		assertEquals("wrong string for total (fiscal year oct.)?", "Total", getProjectCalendar().getShortDateUnit(totalDateUnit, FISCAL_YEAR_START_OCT));
		
		DateUnit dateUnit2005 = new DateUnit("YEARFROM:2005-01");
		assertEquals("wrong year?", "2005", getProjectCalendar().getShortDateUnit(dateUnit2005, FISCAL_YEAR_START_JAN));
		
		DateUnit fY2005 = new DateUnit("YEARFROM:2005-01");
		assertEquals("Fiscal year start wasn't ignored?", "FY05", getProjectCalendar().getShortDateUnit(fY2005, FISCAL_YEAR_START_APR));
		assertEquals("Fiscal year start wasn't ignored?", "FY05", getProjectCalendar().getShortDateUnit(fY2005, FISCAL_YEAR_START_JUL));
		assertEquals("Fiscal year start wasn't ignored?", "FY05", getProjectCalendar().getShortDateUnit(fY2005, FISCAL_YEAR_START_OCT));		
		
		
		DateUnit quarter1Of2009 = new DateUnit("2009Q1");
		assertEquals("wrong quarter?", "Q1", getProjectCalendar().getShortDateUnit(quarter1Of2009, FISCAL_YEAR_START_JAN));
		assertEquals("wrong quarter?", "Q4", getProjectCalendar().getShortDateUnit(quarter1Of2009, FISCAL_YEAR_START_APR));
		assertEquals("wrong quarter?", "Q3", getProjectCalendar().getShortDateUnit(quarter1Of2009, FISCAL_YEAR_START_JUL));
		assertEquals("wrong quarter?", "Q2", getProjectCalendar().getShortDateUnit(quarter1Of2009, FISCAL_YEAR_START_OCT));
		
		DateUnit quarter2Of2009 = new DateUnit("2009Q2");
		assertEquals("wrong quarter?", "Q2", getProjectCalendar().getShortDateUnit(quarter2Of2009, FISCAL_YEAR_START_JAN));
		assertEquals("wrong quarter?", "Q1", getProjectCalendar().getShortDateUnit(quarter2Of2009, FISCAL_YEAR_START_APR));
		assertEquals("wrong quarter?", "Q4", getProjectCalendar().getShortDateUnit(quarter2Of2009, FISCAL_YEAR_START_JUL));
		assertEquals("wrong quarter?", "Q3", getProjectCalendar().getShortDateUnit(quarter2Of2009, FISCAL_YEAR_START_OCT));
		
		DateUnit quarter3Of2009 = new DateUnit("2009Q3");
		assertEquals("wrong quarter?", "Q3", getProjectCalendar().getShortDateUnit(quarter3Of2009, FISCAL_YEAR_START_JAN));
		assertEquals("wrong quarter?", "Q2", getProjectCalendar().getShortDateUnit(quarter3Of2009, FISCAL_YEAR_START_APR));
		assertEquals("wrong quarter?", "Q1", getProjectCalendar().getShortDateUnit(quarter3Of2009, FISCAL_YEAR_START_JUL));
		assertEquals("wrong quarter?", "Q4", getProjectCalendar().getShortDateUnit(quarter3Of2009, FISCAL_YEAR_START_OCT));
		
		DateUnit quarter4Of2009 = new DateUnit("2009Q4");
		assertEquals("wrong quarter?", "Q4", getProjectCalendar().getShortDateUnit(quarter4Of2009, FISCAL_YEAR_START_JAN));
		assertEquals("wrong quarter?", "Q3", getProjectCalendar().getShortDateUnit(quarter4Of2009, FISCAL_YEAR_START_APR));
		assertEquals("wrong quarter?", "Q2", getProjectCalendar().getShortDateUnit(quarter4Of2009, FISCAL_YEAR_START_JUL));
		assertEquals("wrong quarter?", "Q1", getProjectCalendar().getShortDateUnit(quarter4Of2009, FISCAL_YEAR_START_OCT));
		
		
		DateUnit jan2008 = new DateUnit("2008-01");
		assertEquals("wrong month?", "Jan", getProjectCalendar().getShortDateUnit(jan2008, FISCAL_YEAR_START_JAN));
		assertEquals("wrong month?", "Jan", getProjectCalendar().getShortDateUnit(jan2008, FISCAL_YEAR_START_APR));
		assertEquals("wrong month?", "Jan", getProjectCalendar().getShortDateUnit(jan2008, FISCAL_YEAR_START_JUL));
		assertEquals("wrong month?", "Jan", getProjectCalendar().getShortDateUnit(jan2008, FISCAL_YEAR_START_OCT));
		
		DateUnit jul2008 = new DateUnit("2008-07");
		assertEquals("wrong month?", "Jul", getProjectCalendar().getShortDateUnit(jul2008, FISCAL_YEAR_START_JAN));
		assertEquals("wrong month?", "Jul", getProjectCalendar().getShortDateUnit(jul2008, FISCAL_YEAR_START_APR));
		assertEquals("wrong month?", "Jul", getProjectCalendar().getShortDateUnit(jul2008, FISCAL_YEAR_START_JUL));
		assertEquals("wrong month?", "Jul", getProjectCalendar().getShortDateUnit(jul2008, FISCAL_YEAR_START_OCT));
		
		DateUnit dec2008 = new DateUnit("2008-12");
		assertEquals("wrong month?", "Dec", getProjectCalendar().getShortDateUnit(dec2008, FISCAL_YEAR_START_JAN));
		assertEquals("wrong month?", "Dec", getProjectCalendar().getShortDateUnit(dec2008, FISCAL_YEAR_START_APR));
		assertEquals("wrong month?", "Dec", getProjectCalendar().getShortDateUnit(dec2008, FISCAL_YEAR_START_JUL));
		assertEquals("wrong month?", "Dec", getProjectCalendar().getShortDateUnit(dec2008, FISCAL_YEAR_START_OCT));
		
		
		DateUnit janFirst2008 = new DateUnit("2008-01-01");
		assertEquals("wrong day?", "1", getProjectCalendar().getShortDateUnit(janFirst2008, FISCAL_YEAR_START_JAN));
		assertEquals("wrong day (fiscal year oct.)?", "1", getProjectCalendar().getShortDateUnit(janFirst2008, FISCAL_YEAR_START_OCT));
		
		DateUnit lastDayOfDecember = new DateUnit("2008-12-31");
		assertEquals("wrong day?", "31", getProjectCalendar().getShortDateUnit(lastDayOfDecember, FISCAL_YEAR_START_JAN));
		assertEquals("wrong day (Fiscal year Oct.)?", "31", getProjectCalendar().getShortDateUnit(lastDayOfDecember, FISCAL_YEAR_START_OCT));
	}

	private ProjectCalendar getProjectCalendar()
	{
		return getProject().getProjectCalendar();
	}
}
