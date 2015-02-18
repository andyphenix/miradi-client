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
package org.miradi.utils;

import org.martus.util.MultiCalendar;
import org.miradi.main.MiradiTestCase;
import org.miradi.project.ProjectForTesting;

public class TestDateRange extends MiradiTestCase
{
	public TestDateRange(String name) throws Exception
	{
		super(name);
	}
	
	public void test() throws Exception
	{
		storeAndRestore();	
	}
	
	public void testOverlaps() throws Exception
	{
		DateRange dateRange2006 = ProjectForTesting.createDateRange(2006, 2006);
		DateRange dateRange2005To2007 = ProjectForTesting.createDateRange(2005, 2007);
		DateRange dateRange2007To2009 = ProjectForTesting.createDateRange(2007, 2009);

		assertTrue(dateRange2006.overlaps(dateRange2005To2007));
		assertTrue(dateRange2005To2007.overlaps(dateRange2006));	
		assertTrue(dateRange2005To2007.overlaps(dateRange2007To2009)); 
		assertTrue(dateRange2007To2009.overlaps(dateRange2005To2007));
		assertFalse(dateRange2006.overlaps(dateRange2007To2009)); 
		assertFalse(dateRange2007To2009.overlaps(dateRange2006));

	}
	
	public void testCreateFromJson() throws Exception
	{
		DateRange dateRange = getSampleDateRange();
		DateRange dateRangeFromJson = DateRange.createFromJson(dateRange.toJson());
		assertEquals("not same date range?", dateRange, dateRangeFromJson);
			
		assertNull("not null?", DateRange.createFromJson(null));
		assertNull("not null?", DateRange.createFromJson(new EnhancedJsonObject()));
		assertNull("not null?", DateRange.createFromJson(new EnhancedJsonObject("{notRelevant:\"\"}")));
	}
	
	private void storeAndRestore() throws Exception
	{
		MultiCalendar start = MultiCalendar.createFromGregorianYearMonthDay(2006, 12, 1);
		MultiCalendar end = MultiCalendar.createFromGregorianYearMonthDay(2006, 12, 2);
		DateRange dateRange = new DateRange(start, end);
		
		EnhancedJsonObject json = dateRange.toJson();
		
		DateRange dateRange2 = new DateRange(json);
		assertEquals("start date is same?", start, dateRange2.getStartDate());
		assertEquals("end date is same?", end, dateRange2.getEndDate());	
	}
	
	public void testIsWithinBounds() throws Exception
	{
		DateRange boundsDateRange = getSampleDateRange();
		DateRange innerDateRange = createInnerOfSampleDateRange();	
		DateRange partialyInDateRange = createPartialOfSampleDateRange();
		
		assertEquals("is within bounds?", true, boundsDateRange.contains(innerDateRange));
		assertEquals("is within bounds?", false, boundsDateRange.contains(partialyInDateRange));
		assertEquals("contains itself?", true, boundsDateRange.contains(boundsDateRange));		
	}

	public void testContainsAtleastSome() throws Exception
	{
		DateRange boundsDateRange = getSampleDateRange();
		assertEquals("is partially within?", true, boundsDateRange.containsAtleastSome(createPartialOfSampleDateRange()));
		assertEquals("is inner partially within?", true, boundsDateRange.containsAtleastSome(createInnerOfSampleDateRange()));
		assertEquals("is partially within itself?", true, boundsDateRange.containsAtleastSome(boundsDateRange));
		assertEquals("is partially within itself?", false, boundsDateRange.containsAtleastSome(createCompletlyOutsideOfSampleDateRange()));
	}
	
	public void testCombine() throws Exception
	{
		MultiCalendar dateRange1Start = MultiCalendar.createFromGregorianYearMonthDay(2006, 1, 5);
		MultiCalendar dateRange1End = MultiCalendar.createFromGregorianYearMonthDay(2006, 1, 10);
		DateRange dateRange1 = new DateRange(dateRange1Start, dateRange1End);
		
		MultiCalendar dateRange2Start = MultiCalendar.createFromGregorianYearMonthDay(2006, 1, 15);
		MultiCalendar dateRange2End = MultiCalendar.createFromGregorianYearMonthDay(2006, 1, 20);
		DateRange dateRange2 = new DateRange(dateRange2Start, dateRange2End);	
		
		DateRange combined = new DateRange(dateRange1Start, dateRange2End); 
		
		assertEquals("combined date ranges?", combined, DateRange.combine(dateRange1, dateRange2));
		assertEquals("combine with self is same?", dateRange1, DateRange.combine(dateRange1, dateRange1));
	}
	
	public void testGetYearsInBetween() throws Exception
	{
		MultiCalendar date1 = MultiCalendar.createFromGregorianYearMonthDay(2006, 1, 1);
		MultiCalendar date2 = MultiCalendar.createFromGregorianYearMonthDay(2008, 1, 1);
		
		assertEquals("count years in betweem?", 2, DateRange.getYearsInBetween(date1, date2));
	}
	
	public void testToStringForFiscalYears() throws Exception
	{
		MultiCalendar startApril = MultiCalendar.createFromGregorianYearMonthDay(2006, 4, 1);
		MultiCalendar endMarch = MultiCalendar.createFromGregorianYearMonthDay(2007, 3, 31);
		DateRange fyApril = new DateRange(startApril, endMarch);
		assertEquals("FY 2007", fyApril.toString());
	}
	
	public void testIsDay() throws Exception
	{
		assertFalse(createDateRange("2006-01-01", "2006-01-02").isDay());
		assertTrue(createDateRange("2006-01-01", "2006-01-01").isDay());
	}

	public void testIsMonth() throws Exception
	{
		assertFalse(createDateRange("2006-01-01", "2006-02-01").isMonth());
		assertTrue(createDateRange("2006-01-01", "2006-01-31").isMonth());
		assertTrue(createDateRange("2006-12-01", "2006-12-31").isMonth());
	}
	
	public void testIsQuarter() throws Exception
	{
		assertFalse(createDateRange("2006-01-01", "2006-01-31").isQuarter());
		assertFalse(createDateRange("2006-01-01", "2007-03-31").isQuarter());
		assertFalse(createDateRange("2006-01-03", "2006-04-02").isQuarter());
		assertTrue(createDateRange("2006-01-01", "2006-03-31").isQuarter());
		assertTrue(createDateRange("2006-10-01", "2006-12-31").isQuarter());
		assertFalse(createDateRange("2006-02-01", "2006-04-30").isQuarter());
	}
	
	public void testIsYear() throws Exception
	{
		assertFalse(createDateRange("2006-01-01", "2006-12-30").isYear());
		assertFalse(createDateRange("2006-01-01", "2007-12-31").isYear());
		assertFalse(createDateRange("2006-01-02", "2007-01-01").isYear());
		assertTrue(createDateRange("2006-01-01", "2006-12-31").isYear());
		assertTrue(createDateRange("2006-04-01", "2007-03-31").isYear());
		assertFalse(createDateRange("2006-02-01", "2007-01-31").isYear());
	}
	
	private DateRange createDateRange(String startIso, String endIso) throws Exception
	{
		MultiCalendar start = MultiCalendar.createFromIsoDateString(startIso);
		MultiCalendar end = MultiCalendar.createFromIsoDateString(endIso);
		return new DateRange(start, end);
	}
	
	private DateRange createPartialOfSampleDateRange() throws Exception
	{
		MultiCalendar partialInnerStartDate = MultiCalendar.createFromGregorianYearMonthDay(2006, 1, 15);
		MultiCalendar partialInnerEndDate = MultiCalendar.createFromGregorianYearMonthDay(2006, 1, 25);
		DateRange  partialyInDateRange = new DateRange(partialInnerStartDate, partialInnerEndDate);
		return partialyInDateRange;
	}

	private DateRange createInnerOfSampleDateRange() throws Exception
	{
		MultiCalendar innerStartDate = MultiCalendar.createFromGregorianYearMonthDay(2006, 1, 5);
		MultiCalendar innerEndDate = MultiCalendar.createFromGregorianYearMonthDay(2006, 1, 10);
		DateRange innerDateRange = new DateRange(innerStartDate, innerEndDate);
		return innerDateRange;
	}
	
	private DateRange createCompletlyOutsideOfSampleDateRange() throws Exception
	{
		MultiCalendar completelyOutsideStartDate = MultiCalendar.createFromGregorianYearMonthDay(2005, 1, 10);
		MultiCalendar completelyOutsideEndDate = MultiCalendar.createFromGregorianYearMonthDay(2005, 1, 20);
		DateRange  completelyOutsideDateRange = new DateRange(completelyOutsideStartDate, completelyOutsideEndDate);
		return completelyOutsideDateRange;
	}
	
	private DateRange getSampleDateRange() throws Exception
	{
		MultiCalendar boundsStartDate = MultiCalendar.createFromGregorianYearMonthDay(2006, 1, 1);
		MultiCalendar boundsEndDate = MultiCalendar.createFromGregorianYearMonthDay(2006, 1, 20);
		
		DateRange boundsDateRange = new DateRange(boundsStartDate, boundsEndDate);
		return boundsDateRange;
	}
}
