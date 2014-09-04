/* 
Copyright 2005-2014, Foundations of Success, Bethesda, Maryland
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

import org.miradi.objects.TaggedObjectSet;

public class TaggedObjectSetSchemaElement extends BaseObjectSchemaElementWithLabel
{
	public TaggedObjectSetSchemaElement()
	{
		super(TAGGED_OBJECT_SET_ELEMENT_NAME);
		
		createOptionalTextField(TaggedObjectSet.TAG_SHORT_LABEL);
		createOptionalTextField(TaggedObjectSet.TAG_COMMENTS);
		createIdListField(TAGGED_FACTOR_IDS, WRAPPED_BY_DIAGRAM_FACTOR_ELEMENT_NAME);
	}
}
