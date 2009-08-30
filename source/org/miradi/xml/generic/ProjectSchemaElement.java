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

package org.miradi.xml.generic;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;


class ProjectSchemaElement extends SchemaElement
{
	public ProjectSchemaElement()
	{
		objectTypes = new Vector<ObjectSchemaElement>();
		
		objectTypes.add(new ProjectSummarySchemaElement());
	}
	
	public void output(PrintWriter writer) throws IOException
	{
		writer.println(getDotElement(getProjectElementName()) + " = ");
		super.output(writer);
		writer.println("miradi:" + getProjectElementName());
		writer.println("{");
		for(ObjectSchemaElement objectElement: objectTypes)
		{
			writer.println(getDotElement(objectElement.getObjectTypeName()) + "&");
		}
		writer.println("}");
		writer.println();
		
		for(ObjectSchemaElement objectElement: objectTypes)
		{
			objectElement.output(writer);
		}
		
	}
	
	String getProjectElementName()
	{
		return "conservation_project";
	}

	private Vector<ObjectSchemaElement> objectTypes;
}