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

import java.util.Vector;

import org.miradi.xml.xmpz2.Xmpz2XmlConstants;

public class Xmpz2CustomSchemaDefinitionCreator implements Xmpz2XmlConstants
{
	public Xmpz2CustomSchemaDefinitionCreator(Xmpz2SchemaWriter xmpz2SchemaWriterToUse, String parentNameToUse)
	{
		xmpz2SchemaWriter = xmpz2SchemaWriterToUse;
		parentName = parentNameToUse;
		childElements = new Vector<String>();
	}
	
	public void addChildElement(String childElement)
	{
		childElements.add(childElement);
	}
	
	public void addChildElement(String elementName, String elementType)
	{
		addChildElement(getSchemaWriter().createSchemaElement(elementName, elementType));
	}
	
	public void addOptionalChildElement(String elementName, String elementType)
	{
		addChildElement(getSchemaWriter().createOptionalSchemaElement(elementName, elementType));
	}
	
	public void addTextAttributeElement(String elementName)
	{
		addChildElement(getSchemaWriter().createTextAttributeElement(elementName));
	}
	
	public void addOptionalTextSchemaElement(String elementName)
	{
		addChildElement(getSchemaWriter().createTextSchemaElement(EXTRA_DATA_ITEM_VALUE));
	}
	
	public void addZeroOrMoreDotElement(String elementName)
	{
		addChildElement(getSchemaWriter().createZeroOrMoreDotElement(elementName));
	}
	
	public String createSchemaElement()
	{
		String schemaElement = getSchemaWriter().createAlias(getSchemaWriter().createElementName(parentName), ELEMENT_NAME + PREFIX + parentName);
		schemaElement = addNewLine(schemaElement);
		schemaElement = addStartBlock(schemaElement);
		schemaElement = addNewLine(schemaElement);
		for (int index = 0; index < childElements.size(); ++index)
		{
			if (index > 0)
			{
				schemaElement += " &";
				schemaElement = addNewLine(schemaElement);
			}
			
			schemaElement += getSchemaWriter().INDENTATION + childElements.get(index);
		}
		schemaElement = addNewLine(schemaElement);
		schemaElement = addEndBlock(schemaElement);
		schemaElement = addNewLine(schemaElement);
		schemaElement = addNewLine(schemaElement);
		
		return schemaElement;
	}

	private String addNewLine(String schemaElement)
	{
		return schemaElement += "\n";
	}
	
	private String addStartBlock(String schemaElement)
	{
		return schemaElement += "{";
	}
	
	private String addEndBlock(String schemaElement)
	{
		return schemaElement += "}";
	}
	
	private Xmpz2SchemaWriter getSchemaWriter()
	{
		return xmpz2SchemaWriter;
	}
	
	private Vector<String> childElements;
	private Xmpz2SchemaWriter xmpz2SchemaWriter;
	private String parentName;
}
