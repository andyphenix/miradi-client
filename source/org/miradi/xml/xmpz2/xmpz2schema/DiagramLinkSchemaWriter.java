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

package org.miradi.xml.xmpz2.xmpz2schema;

import org.miradi.objects.DiagramLink;
import org.miradi.schemas.BaseObjectSchema;

public class DiagramLinkSchemaWriter extends BaseObjectSchemaWriter
{
	public DiagramLinkSchemaWriter(Xmpz2XmlSchemaCreator creatorToUse, BaseObjectSchema baseObjectSchemaToUse)
	{
		super(creatorToUse, baseObjectSchemaToUse);
	}

	@Override
	protected boolean shouldOmitField(String tag)
	{
		//NOTE: FactorLink does not exist in the XMPZ world, and will be removed from Miradi eventually
		if (tag.equals(DiagramLink.TAG_WRAPPED_ID))
			return true;
		
		return super.shouldOmitField(tag);
	}
}
