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

import org.miradi.objects.BaseObject;
import org.miradi.project.Project;
import org.miradi.schemas.AbstractFieldSchema;
import org.miradi.schemas.BaseObjectSchema;
import org.miradi.xml.wcs.XmpzXmlConstants;

public class BaseObjectExporter implements XmpzXmlConstants
{
	public BaseObjectExporter(final Xmpz2XmlUnicodeWriter writerToUse)
	{
		writer = writerToUse;
	}
	
	public void writeBaseObjectDataSchemaElement(final BaseObject baseObject) throws Exception
	{
		BaseObjectSchema baseObjectSchema = baseObject.getSchema();
		getWriter().writeObjectElementStart(baseObject);
		writeFields(baseObject, baseObjectSchema);
		getWriter().writeObjectElementEnd(baseObjectSchema);
	}

	protected void writeFields(final BaseObject baseObject,	BaseObjectSchema baseObjectSchema) throws Exception
	{
		for(AbstractFieldSchema fieldSchema : baseObjectSchema)
		{
			if (!doesFieldRequireSpecialHandling(fieldSchema.getTag()))
			{
				getWriter().writeFieldElement(baseObject, fieldSchema);
			}
		}
	}

	protected boolean doesFieldRequireSpecialHandling(final String tag)
	{
		return false;
	}

	protected Xmpz2XmlUnicodeWriter getWriter()
	{
		return writer;
	}
	
	protected Project getProject()
	{
		return getWriter().getProject();
	}

	private Xmpz2XmlUnicodeWriter writer;
}