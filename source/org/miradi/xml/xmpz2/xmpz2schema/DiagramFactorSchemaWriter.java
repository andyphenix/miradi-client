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

import org.miradi.objects.DiagramFactor;
import org.miradi.schemas.BaseObjectSchema;

public class DiagramFactorSchemaWriter extends BaseObjectSchemaWriter
{
	public DiagramFactorSchemaWriter(Xmpz2XmlSchemaCreator creatorToUse, BaseObjectSchema baseObjectSchemaToUse)
	{
		super(creatorToUse, baseObjectSchemaToUse);
	}
	
	@Override
	protected boolean doesFieldRequireSpecialHandling(String tag)
	{
		if (tag.equals(DiagramFactor.TAG_FONT_STYLE))
			return true;
		
		if (tag.equals(DiagramFactor.TAG_BACKGROUND_COLOR))
			return true;
		
		if (tag.equals(DiagramFactor.TAG_FONT_SIZE))
			return true;
		
		if (tag.equals(DiagramFactor.TAG_FOREGROUND_COLOR))
			return true;
				
		return super.doesFieldRequireSpecialHandling(tag);
	}
	
	@Override
	protected Vector<String> createCustomSchemaFields()
	{
		Vector<String> schemaElements = super.createCustomSchemaFields();
		
		schemaElements.add(getXmpz2XmlSchemaCreator().getSchemaWriter().createSchemaElement(getXmpz2ElementName(), STYLING, STYLING + ".element"));
		
		return schemaElements;
	}
}
