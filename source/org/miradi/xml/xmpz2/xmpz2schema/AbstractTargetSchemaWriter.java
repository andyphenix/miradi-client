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

import org.miradi.schemas.BaseObjectSchema;
import org.miradi.xml.generic.XmlSchemaCreator;

public class AbstractTargetSchemaWriter extends BaseObjectSchemaWriter
{
	public AbstractTargetSchemaWriter(Xmpz2XmlSchemaCreator creatorToUse, BaseObjectSchema baseObjectSchemaToUse)
	{
		super(creatorToUse, baseObjectSchemaToUse);
	}
	
	@Override
	public Vector<String> createFieldSchemas() throws Exception
	{
		Vector<String> schemaElements = super.createFieldSchemas();
		
		schemaElements.add(getXmpz2XmlSchemaCreator().createSchemaElement(getXmpz2ElementName(), TARGET_THREAT_RATING, XmlSchemaCreator.VOCABULARY_THREAT_RATING));
		schemaElements.add(getXmpz2XmlSchemaCreator().createSchemaElement(getXmpz2ElementName(), TARGET_STATUS_ELEMENT_NAME, VOCABULARY_TARGET_STATUS));
		
		return schemaElements;
	}	
}