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

package org.miradi.schemas;

import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.Target;
import org.miradi.questions.HabitatAssociationQuestion;

public class TargetSchema extends AbstractTargetSchema
{
	public TargetSchema()
	{
		super();
	}
	
	@Override
	protected void fillFieldSchemas()
	{
		super.fillFieldSchemas();
		
		createOwnedFieldSchemaReflist(Target.TAG_STRESS_REFS, STRESS);
		createFieldSchemaSingleLineUserText(Target.TAG_SPECIES_LATIN_NAME);
		createFieldSchemaCodeList(Target.TAG_HABITAT_ASSOCIATION, getQuestion(HabitatAssociationQuestion.class));
		
		createPseudoFieldSchemaQuestion(Target.PSEUDO_TAG_HABITAT_ASSOCIATION_VALUE);
	}

	public static int getObjectType()
	{
		return ObjectType.TARGET;
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
	
	@Override
	public String getXmpz2ElementName()
	{
		return BIODIVERSITY_TARGET;
	}
	
	public static final String OBJECT_NAME = "Target";
}
