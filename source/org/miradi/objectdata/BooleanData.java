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
package org.miradi.objectdata;

import org.miradi.objecthelpers.ORef;
import org.miradi.schemas.AbstractFieldSchema;
import org.miradi.schemas.BaseObjectSchema;
import org.miradi.xml.xmpz2.Xmpz2XmlImporter;
import org.miradi.xml.xmpz2.Xmpz2XmlWriter;
import org.miradi.xml.xmpz2.xmpz2schema.Xmpz2XmlSchemaCreator;
import org.w3c.dom.Node;

public class BooleanData extends AbstractIntegerData
{
	public BooleanData(String tagToUse)
	{
		super(tagToUse);
	}
	
	@Override
	public boolean isBooleanData()
	{
		return true;
	}
	
	public boolean asBoolean()
	{
		if (get().length() == 0)
			return false;
		
		if (get().equals(BOOLEAN_FALSE))
			return false;
		
		if (get().equals(BOOLEAN_TRUE))
			return true;
		
		throw new RuntimeException("Invalid boolean value :" + get());
	}
	
	public static String toString(boolean booleanToConvert)
	{
		if (booleanToConvert == true)
			return BOOLEAN_TRUE;
		
		return "";
	}
	
	@Override
	public void set(String newValue) throws Exception
	{
		super.set(newValue);
		if (asInt()<0 || asInt()>1)
			throw new RuntimeException("Invalid boolean value :" + newValue);
	}
	
	@Override
	public void writeAsXmpz2XmlData(Xmpz2XmlWriter writer, BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema) throws Exception
	{
		writer.writeBooleanData(baseObjectSchema, fieldSchema, get());
	}
	
	@Override
	public void readAsXmpz2XmlData(Xmpz2XmlImporter importer, Node node, ORef destinationRefToUse, BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema) throws Exception
	{
		importer.importBooleanField(node, destinationRefToUse, baseObjectSchema, fieldSchema);
	}
	
	@Override
	public String createXmpz2SchemaElementString(Xmpz2XmlSchemaCreator creator, BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema) throws Exception
	{
		return creator.createBooleanSchemaElement(baseObjectSchema, fieldSchema);
	}
	
	static public final String BOOLEAN_FALSE = "";
	static public final String BOOLEAN_TRUE = "1";
}
