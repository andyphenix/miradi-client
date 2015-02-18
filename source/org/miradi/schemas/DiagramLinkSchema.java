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

package org.miradi.schemas;

import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.DiagramLink;
import org.miradi.questions.DiagramLinkColorQuestion;

public class DiagramLinkSchema extends BaseObjectSchema
{
	public DiagramLinkSchema()
	{
		super();
	}
	
	@Override
	protected void fillFieldSchemas()
	{
		super.fillFieldSchemas();

		createFieldSchemaRequiredBaseId(DiagramLink.TAG_WRAPPED_ID, FactorLinkSchema.getObjectType());
		createFieldSchemaRequiredBaseId(DiagramLink.TAG_FROM_DIAGRAM_FACTOR_ID, DiagramFactorSchema.getObjectType());
		createFieldSchemaRequiredBaseId(DiagramLink.TAG_TO_DIAGRAM_FACTOR_ID, DiagramFactorSchema.getObjectType());
		createFieldSchemaPointList(DiagramLink.TAG_BEND_POINTS);
		createFieldSchemaReflist(DiagramLink.TAG_GROUPED_DIAGRAM_LINK_REFS, DIAGRAM_LINK);
		createFieldSchemaChoice(DiagramLink.TAG_COLOR, DiagramLinkColorQuestion.class);
		createFieldSchemaBoolean(DiagramLink.TAG_IS_BIDIRECTIONAL_LINK);
	}

	public static int getObjectType()
	{
		return ObjectType.DIAGRAM_LINK;
	}
	
	@Override
	public int getType()
	{
		return getObjectType();
	}

	@Override
	public String getObjectName()
	{
		return OBJECT_NAME;
	}
	
	public static final String OBJECT_NAME = "DiagramLink";
}
