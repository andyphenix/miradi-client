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

package org.miradi.xml.xmpz2.xmpz2schema;

import java.util.Collections;
import java.util.HashSet;
import java.util.Vector;

import org.miradi.objectdata.ObjectData;
import org.miradi.schemas.AbstractFieldSchema;
import org.miradi.schemas.BaseObjectSchema;
import org.miradi.xml.xmpz2.Xmpz2XmlConstants;
import org.miradi.xml.xmpz2.Xmpz2XmlWriter;

public class BaseObjectSchemaWriter implements Xmpz2XmlConstants
{
	public BaseObjectSchemaWriter(Xmpz2XmlSchemaCreator creatorToUse, BaseObjectSchema baseObjectSchemaToUse)
	{
		creator = creatorToUse;
		baseObjectSchema = baseObjectSchemaToUse;
	}
	
	public Vector<String> createFieldSchemas() throws Exception
	{
		HashSet<String> fieldSchemasAsString = new HashSet<String>();
		if (hasIdAttributeElement())
			fieldSchemasAsString.add("attribute " + ID + " "+ "{xsd:integer}");
		
		for(AbstractFieldSchema fieldSchema : getBaseObjectSchema())
		{
			ObjectData objectData = fieldSchema.createField(null);
			if (objectData.isPseudoField())
				continue;
			
			if (shouldOmitField(fieldSchema.getTag()))
				continue;

			if (doesFieldRequireSpecialHandling(fieldSchema.getTag()))
				continue;
			
			fieldSchemasAsString.add(objectData.createXmpz2SchemaElementString(creator, baseObjectSchema, fieldSchema));
		}
		
		fieldSchemasAsString.addAll(writeCustomField());
		final Vector<String> sortedFieldSchemas = new Vector<String>(fieldSchemasAsString);
		Collections.sort(sortedFieldSchemas);
		
		return sortedFieldSchemas;
	}
	
	protected boolean shouldOmitField(String tag)
	{
		return false;
	}

	protected Vector<String> writeCustomField()
	{
		return new Vector<String>();
	}

	protected boolean hasIdAttributeElement()
	{
		return true;
	}

	protected boolean doesFieldRequireSpecialHandling(String tag)
	{
		return false;
	}
	
	protected Xmpz2XmlSchemaCreator getCreator()
	{
		return creator;
	}

	public String getPoolName()
	{
		return Xmpz2XmlWriter.createPoolElementName(getXmpz2ElementName());
	}
	
	public String getXmpz2ElementName()
	{
		return getBaseObjectSchema().getXmpz2ElementName();
	}
	
	public BaseObjectSchema getBaseObjectSchema()
	{
		return baseObjectSchema;
	}
	
	private Xmpz2XmlSchemaCreator creator;
	private BaseObjectSchema baseObjectSchema;
}
