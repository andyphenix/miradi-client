/* 
Copyright 2005-2013, Foundations of Success, Bethesda, Maryland 
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
import org.miradi.schemas.MiradiShareTaxonomySchema;

public class MiradiShareTaxonomySchemaWriter extends BaseObjectSchemaWriter
{
	public MiradiShareTaxonomySchemaWriter(Xmpz2XmlSchemaCreator creatorToUse, BaseObjectSchema baseObjectSchemaToUse)
	{
		super(creatorToUse, baseObjectSchemaToUse);
	}
	
	@Override
	public Vector<String> createFieldSchemas() throws Exception
	{
		final Vector<String> fieldSchemasAsString = super.createFieldSchemas();
		fieldSchemasAsString.add(getXmpz2XmlSchemaCreator().getSchemaWriter().createTextAttributeElement(TAXONOMY_CODE));
		fieldSchemasAsString.add(getXmpz2XmlSchemaCreator().getSchemaWriter().createRequiredParentAndZeroOrMoreElementDefinition(TAXONOMY_ELEMENTS, TAXONOMY_ELEMENT));
		fieldSchemasAsString.add(getXmpz2XmlSchemaCreator().getSchemaWriter().createTaxonomyElementCode(TAXONOMY_TOP_LEVEL_ELEMENT_CODES));
		
		return fieldSchemasAsString;
	}
	
	@Override
	protected boolean doesFieldRequireSpecialHandling(String tag)
	{
		if (tag.equals(MiradiShareTaxonomySchema.TAG_TAXONOMY_ELEMENTS))
			return true;
		
		if (tag.equals(MiradiShareTaxonomySchema.TAG_TAXONOMY_TOP_LEVEL_ELEMENT_CODES))
			return true;
		
		return super.doesFieldRequireSpecialHandling(tag);
	}
	
	@Override
	protected boolean shouldOmitField(String tag)
	{
		if (tag.equals(MiradiShareTaxonomySchema.TAG_TAXONOMY_CODE))
			return true;
		
		return super.shouldOmitField(tag);
	}
	
	@Override
	protected boolean hasIdAttributeElement()
	{
		return false;
	}
}
