/* 
Copyright 2005-2012, Foundations of Success, Bethesda, Maryland 
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

package org.miradi.xml.xmpz2;

import java.util.Vector;

import org.miradi.objecthelpers.CategorizedQuantity;
import org.miradi.objecthelpers.TimePeriodCosts;
import org.miradi.xml.wcs.XmpzXmlConstants;

public class WorkUnitsTimePeriodCostsWriter2 extends AbstractTimePeriodCostsWriter2
{
	public WorkUnitsTimePeriodCostsWriter2(Xmpz2XmlWriter writerToUse)
	{
		super(writerToUse);
	}
	
	@Override
	protected Vector<CategorizedQuantity> getCategorizedQuantaties(TimePeriodCosts timePeriodCosts)
	{
		return timePeriodCosts.getWorkUnitCategorizedQuantities();
	}

	@Override
	protected String getDateUnitElementName()
	{
		return XmpzXmlConstants.WORK_UNITS_DATE_UNIT;
	}

	@Override
	protected String getQuantityDateUnitElementName()
	{
		return XmpzXmlConstants.DATE_UNIT_WORK_UNITS;
	}
	
	@Override
	protected String getDayElementName()
	{
		return WORK_UNITS_DAY;
	}
	
	@Override
	protected String getMonthElementName()
	{
		return WORK_UNITS_MONTH;
	}
	
	@Override
	protected String getQuarterElementName()
	{
		return WORK_UNITS_QUARTER;
	}
	
	@Override
	protected String getYearElementName()
	{
		return WORK_UNITS_YEAR;
	}
	
	@Override
	protected String getFullProjectTimespanElementName()
	{
		return WORK_UNITS_FULL_PROJECT_TIMESPAN;
	}
	
	@Override
	protected String getQuantatityElementName()
	{
		return XmpzXmlConstants.WORK_UNITS;
	}

	@Override
	protected String getCalculatedEntriesElementName()
	{
		return CALCULATED_WORK_UNITS_ENTRIES;
	}

	@Override
	protected String getEntryElementName()
	{
		return WORK_UNITS_ENTRY;
	}
}
