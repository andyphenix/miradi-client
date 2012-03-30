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
package org.miradi.objectdata;

import org.miradi.schemas.AbstractFieldSchema;
import org.miradi.schemas.BaseObjectSchema;
import org.miradi.utils.DateRange;
import org.miradi.utils.EnhancedJsonObject;
import org.miradi.xml.xmpz2.Xmpz2XmlUnicodeWriter;

public class DateRangeData extends ObjectData
{
	public DateRangeData(String tagToUse)
	{
		super(tagToUse);
		dateRange = null;
	}

	@Override
	public void set(String newValue) throws Exception
	{
		if(newValue.length() == 0)
		{
			dateRange = null;
			return;
		}
		
		dateRange = new DateRange(new EnhancedJsonObject(newValue));
	}

	@Override
	public String get()
	{
		if(dateRange == null)
			return "";
		
		return dateRange.toJson().toString();
	}
	
	public DateRange getDateRange()
	{
		return dateRange;
	}

	@Override
	public boolean equals(Object rawOther)
	{
		if(!(rawOther instanceof DateData))
			return false;
		
		DateRangeData other = (DateRangeData)rawOther;
		return dateRange.equals(other.dateRange);
	}

	@Override
	public int hashCode()
	{
		return dateRange.hashCode();
	}
	
	@Override
	public void writeAsXmpz2XmlData(Xmpz2XmlUnicodeWriter writer, BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema) throws Exception
	{
		writer.writeDateRangeData(baseObjectSchema, fieldSchema, get());
	}
	
	private DateRange dateRange;
}
