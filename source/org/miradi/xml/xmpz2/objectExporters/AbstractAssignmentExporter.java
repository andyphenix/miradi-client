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

package org.miradi.xml.xmpz2.objectExporters;

import org.miradi.objecthelpers.DateUnit;
import org.miradi.objects.Assignment;
import org.miradi.objects.BaseObject;
import org.miradi.schemas.BaseObjectSchema;
import org.miradi.utils.DateUnitEffort;
import org.miradi.utils.DateUnitEffortList;
import org.miradi.utils.DoubleUtilities;
import org.miradi.xml.xmpz2.BaseObjectExporter;
import org.miradi.xml.xmpz2.Xmpz2XmlWriter;

abstract public class AbstractAssignmentExporter extends BaseObjectExporter
{
	public AbstractAssignmentExporter(Xmpz2XmlWriter writerToUse, final int objectTypeToUse)
	{
		super(writerToUse, objectTypeToUse);
	}
	
	@Override
	protected void writeFields(final BaseObject baseObject,	BaseObjectSchema baseObjectSchema) throws Exception
	{
		super.writeFields(baseObject, baseObjectSchema);

		Assignment assignment = (Assignment) baseObject;
		if (assignment.getDateUnitEffortList().size() > 0)
			exportDateUnitEfforList(assignment.getDateUnitEffortList(), getDateUnitsElementName());
	}
	
	@Override
	protected boolean doesFieldRequireSpecialHandling(String tag)
	{
		if (tag.equals(Assignment.TAG_DATEUNIT_EFFORTS))
			return true;
		
		return super.doesFieldRequireSpecialHandling(tag);
	}
	
	private void exportDateUnitEfforList(DateUnitEffortList dateUnitEffortList, String dateUnitsElementName) throws Exception
	{
		final String dateUnitEffortsElementName = getPoolName() + Assignment.TAG_DATEUNIT_EFFORTS;
		getWriter().writeStartElement(dateUnitEffortsElementName);
		for (int index = 0; index < dateUnitEffortList.size(); ++index)
		{
			DateUnitEffort dateUnitEffort = dateUnitEffortList.getDateUnitEffort(index);
			getWriter().writeStartElement(dateUnitsElementName);
			
			writeDateUnit(dateUnitEffort.getDateUnit());
			writeQuantity(dateUnitEffort.getQuantity());
			
			getWriter().writeEndElement(dateUnitsElementName);
		}
		getWriter().writeEndElement(dateUnitEffortsElementName);
	}

	private void writeDateUnit(DateUnit dateUnit) throws Exception
	{
		getWriter().writeStartElement(getDateUnitElementName());
		
		if (dateUnit.isProjectTotal())
			getWriter().writeProjectTotal(dateUnit, getFullProjectTimespanElementName());
		
		if (dateUnit.isYear())
			getWriter().writeYear(dateUnit, getYearElementName());
		
		if (dateUnit.isQuarter())
			getWriter().writeQuarter(dateUnit, getQuarterElementName());
		
		if (dateUnit.isMonth())
			getWriter().writeMonth(dateUnit, getMonthElementName());
		
		if (dateUnit.isDay())
			getWriter().writeDay(dateUnit, getDayElementName());
		
		getWriter().writeEndElement(getDateUnitElementName());
	}
	
	private void writeQuantity(double expense) throws Exception
	{
		getWriter().writeStartElement(getQuantatityElementName());
		
		String formattedForExpense = DoubleUtilities.toStringForData(expense);
		getWriter().write(formattedForExpense);
		
		getWriter().writeEndElement(getQuantatityElementName());
	}
	
	abstract protected String getDateUnitElementName();
	
	abstract protected String getDayElementName();
	
	abstract protected String getMonthElementName();
	
	abstract protected String getQuarterElementName();
	
	abstract protected String getYearElementName();
	
	abstract protected String getFullProjectTimespanElementName();
	
	abstract protected String getQuantatityElementName();
	
	abstract protected String getDateUnitsElementName();
	
	abstract protected String getPoolName();
}
