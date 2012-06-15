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

package org.miradi.xml.wcs;

import org.martus.util.MultiCalendar;
import org.martus.util.UnicodeWriter;
import org.miradi.objecthelpers.DateUnit;
import org.miradi.objects.Assignment;
import org.miradi.objects.BaseObject;
import org.miradi.utils.DateUnitEffort;
import org.miradi.utils.DateUnitEffortList;
import org.miradi.utils.DoubleUtilities;

abstract public class AbstractAssignmentPoolExporter extends BaseObjectPoolExporter
{
	public AbstractAssignmentPoolExporter(XmpzXmlExporter wcsXmlExporterToUse, String containerNameToUse, int objectTypeToUse)
	{
		super(wcsXmlExporterToUse, containerNameToUse, objectTypeToUse);
	}
	
	@Override
	protected void exportFields(UnicodeWriter writer, BaseObject baseObject) throws Exception
	{
		super.exportFields(writer, baseObject);
		
		Assignment assignment = (Assignment) baseObject;
		exportId(assignment.getFundingSourceRef(), FUNDING_SOURCE_ID);
		exportId(assignment.getAccountingCodeRef(), ACCOUNTING_CODE_ID);
		exportId(assignment.getCategoryOneRef(), BUDGET_CATEGORY_ONE_ID);
		exportId(assignment.getCategoryTwoRef(), BUDGET_CATEGORY_TWO_ID);
	}	
	
	protected void exportDateUnitEfforList(DateUnitEffortList dateUnitEffortList, String dateUnitsElementName) throws Exception
	{
		if (dateUnitEffortList.size() == 0)
			return;
		
		getWcsXmlExporter().writeStartElement(getPoolName() + Assignment.TAG_DATEUNIT_EFFORTS);
		for (int index = 0; index < dateUnitEffortList.size(); ++index)
		{
			DateUnitEffort dateUnitEffort = dateUnitEffortList.getDateUnitEffort(index);
			getWcsXmlExporter().writeStartElement(dateUnitsElementName);
			
			writeDateUnit(dateUnitEffort.getDateUnit());
			writeQuantity(dateUnitEffort.getQuantity());
			
			getWcsXmlExporter().writeEndElement(dateUnitsElementName);
		}
		getWcsXmlExporter().writeEndElement(getPoolName() + Assignment.TAG_DATEUNIT_EFFORTS);
	}

	private void writeDateUnit(DateUnit dateUnit) throws Exception
	{
		getWcsXmlExporter().writeStartElement(getDateUnitElementName());
		
		if (dateUnit.isProjectTotal())
			writeProjectTotal(dateUnit);
		
		if (dateUnit.isYear())
			writeYear(dateUnit);
		
		if (dateUnit.isQuarter())
			writeQuarter(dateUnit);
		
		if (dateUnit.isMonth())
			writeMonth(dateUnit);
		
		if (dateUnit.isDay())
			writeDay(dateUnit);
		
		getWcsXmlExporter().writeEndElement(getDateUnitElementName());
	}

	private void writeDay(DateUnit dateUnit) throws Exception
	{
		getWcsXmlExporter().writeStartElementWithAttribute(getWriter(), getDayElementName(), DATE, dateUnit.toString());
		getWcsXmlExporter().writeEndElement(getDayElementName());
	}


	private void writeMonth(DateUnit dateUnit) throws Exception
	{
		getWcsXmlExporter().writeStartElementWithTwoAttributes(getWriter(), getMonthElementName(), YEAR, dateUnit.getYear(), MONTH, dateUnit.getMonth());
		getWcsXmlExporter().writeEndElement(getMonthElementName());
	}

	private void writeQuarter(DateUnit dateUnit) throws Exception
	{
		MultiCalendar start = dateUnit.getQuarterDateRange().getStartDate();
		getWcsXmlExporter().writeStartElementWithTwoAttributes(getWriter(), getQuarterElementName(), YEAR, start.getGregorianYear(), START_MONTH, start.getGregorianMonth());
		getWcsXmlExporter().writeEndElement(getQuarterElementName());
	}

	private void writeYear(DateUnit dateUnit) throws Exception
	{
		int yearStartMonth = dateUnit.getYearStartMonth();
		int year2 = Integer.parseInt(dateUnit.getYearYearString());
		getWcsXmlExporter().writeStartElementWithTwoAttributes(getWriter(), getYearElementName(), START_YEAR, year2, START_MONTH, yearStartMonth);
		getWcsXmlExporter().writeEndElement(getYearElementName());
	}

	private void writeProjectTotal(DateUnit dateUnit) throws Exception
	{		
		getWcsXmlExporter().writeStartElementWithAttribute(getWriter(), getFullProjectTimespanElementName(), FULL_PROJECT_TIMESPAN, "Total");
		getWcsXmlExporter().writeEndElement(getFullProjectTimespanElementName());
	}

	private void writeQuantity(double expense) throws Exception
	{
		getWcsXmlExporter().writeStartElement(getQuantatityElementName());
		
		String formattedForExpense = DoubleUtilities.toStringForData(expense);
		getWriter().write(formattedForExpense);
		
		getWcsXmlExporter().writeEndElement(getQuantatityElementName());
	}
	
	abstract protected String getDateUnitElementName();
	
	abstract protected String getDayElementName();
	
	abstract protected String getMonthElementName();
	
	abstract protected String getQuarterElementName();
	
	abstract protected String getYearElementName();
	
	abstract protected String getFullProjectTimespanElementName();
	
	abstract protected String getQuantatityElementName();
}
