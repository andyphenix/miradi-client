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

import java.util.Vector;

import org.miradi.objecthelpers.DateUnit;
import org.miradi.schemas.AbstractFieldSchema;
import org.miradi.schemas.BaseObjectSchema;
import org.miradi.utils.CodeList;
import org.miradi.xml.xmpz2.Xmpz2XmlUnicodeWriter;

public class DateUnitListData extends AbstractStringListData
{
	public DateUnitListData(String tagToUse)
	{
		super(tagToUse);
	}

	public Vector<DateUnit> getDateUnits()
	{
		Vector<DateUnit> dateUnits = new Vector<DateUnit>();
		CodeList codes = getCodeList();
		for (int index = 0; index < codes.size(); ++index)
		{
			dateUnits.add(new DateUnit(codes.get(index))); 
		}
		
		return dateUnits;
	}
	
	public static CodeList convertToCodeList(Vector<DateUnit> dateUnits)
	{
		CodeList dateUnitsAsCodeList = new CodeList();
		for (int index = 0; index < dateUnits.size(); ++index)
		{
			dateUnitsAsCodeList.add(dateUnits.get(index).getDateUnitCode());
		}
		
		return dateUnitsAsCodeList;
	}
	
	@Override
	public void writeAsXmpz2XmlData(Xmpz2XmlUnicodeWriter writer, BaseObjectSchema schema, AbstractFieldSchema fieldSchema) throws Exception
	{
		writer.writeDateUnitListData(schema, fieldSchema, get());
	}
}
